package com.wwc2.ttxassist.mediacodec;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AudioRecordEncoder {
    private static final String TAG = "AudioRecordEncoder";
    public static final String MIMETYPE_AUDIO_AAC = "audio/mp4a-latm";
    // 输入源 麦克风
    private final static int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    // 采样率 44100Hz，所有设备都支持
    private final static int SAMPLE_RATE = 16000;
    // 通道 单声道，所有设备都支持
    private final static int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    // 精度 16 位，所有设备都支持
    private final static int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    // 通道数 单声道
    private static final int CHANNEL_COUNT = 1;
    // 比特率
    private static final int BIT_RATE = 19850;//96_000;
    // 缓冲区字节大小
    private int mBufferSizeInBytes;
    private AudioRecord mAudioRecord;
    private MediaCodec mMediaCodec;
    private volatile boolean mStopped;
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private IAudioDataCallback mIAudioDataCallback;

    /**
     * 创建录音对象
     */
    public void createAudio() {
        // 获得缓冲区字节大小
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(AudioRecordEncoder.SAMPLE_RATE, AudioRecordEncoder.CHANNEL_CONFIG, AudioRecordEncoder.AUDIO_FORMAT);
        if (mBufferSizeInBytes <= 0) {
            throw new RuntimeException("AudioRecord is not available, minBufferSize: " + mBufferSizeInBytes);
        }
        Log.i(TAG, "createAudioRecord minBufferSize: " + mBufferSizeInBytes);

        mAudioRecord = new AudioRecord(AudioRecordEncoder.AUDIO_SOURCE, AudioRecordEncoder.SAMPLE_RATE, AudioRecordEncoder.CHANNEL_CONFIG, AudioRecordEncoder.AUDIO_FORMAT, mBufferSizeInBytes);
        int state = mAudioRecord.getState();
        Log.i(TAG, "createAudio state: " + state + ", initialized: " + (state == AudioRecord.STATE_INITIALIZED));
    }

    public void createMediaCodec() throws IOException {
        MediaCodecInfo mediaCodecInfo = CodecUtils.selectCodec(MIMETYPE_AUDIO_AAC);
        if (mediaCodecInfo == null) {
            throw new RuntimeException(MIMETYPE_AUDIO_AAC + " encoder is not available");
        }
        Log.i(TAG, "createMediaCodec: mediaCodecInfo " + mediaCodecInfo.getName());

        MediaFormat format = MediaFormat.createAudioFormat(MIMETYPE_AUDIO_AAC, SAMPLE_RATE, CHANNEL_COUNT);
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        mMediaCodec = MediaCodec.createEncoderByType(MIMETYPE_AUDIO_AAC);
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    public void setCodecDataLister(IAudioDataCallback lister){
        mIAudioDataCallback= lister;
    }


    public void start(final File outFile) throws IOException {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    startAsync(outFile);
                } catch (IOException e) {
                    Log.e(TAG, "startAsync: ", e);
                }
            }
        });
    }

    private void startAsync(File outFile) throws IOException {
        Log.d(TAG, "start() called with: outFile = [" + outFile + "]");
        mStopped = false;
        //try (OutputStream fos = new FileOutputStream(outFile)) {
            mMediaCodec.start();
            mAudioRecord.startRecording();

            ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
            MediaCodec.BufferInfo outBufferInfo = new MediaCodec.BufferInfo();
            final long timeoutUs = 10_000;
            while (!mStopped) {
                int inputBufIndex = mMediaCodec.dequeueInputBuffer(timeoutUs);
                if (inputBufIndex >= 0) {
                    ByteBuffer inputBuffer = inputBuffers[inputBufIndex];
                    inputBuffer.clear();
                    int remaining = inputBuffer.remaining();
                    int bufSize = Math.min(remaining, mBufferSizeInBytes);
                    byte[] buffer = new byte[bufSize];
                    int readSize = mAudioRecord.read(buffer, 0, buffer.length);
                    if (readSize >= 0) {
                        inputBuffer.put(buffer);
                        inputBuffer.limit(buffer.length);
                        mMediaCodec.queueInputBuffer(inputBufIndex, 0, readSize, 0, 0);
                    }

                    int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(outBufferInfo, timeoutUs);
                    while (outputBufferIndex >= 0) {
                        ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                        outputBuffer.position(outBufferInfo.offset);
                        outputBuffer.limit(outBufferInfo.offset + outBufferInfo.size);
                        byte[] chunkAudio = new byte[outBufferInfo.size + 7];// 7 is ADTS size
                        addADTStoPacket(chunkAudio, chunkAudio.length);
                        outputBuffer.get(chunkAudio, 7, outBufferInfo.size);
                        outputBuffer.position(outBufferInfo.offset);
                        mIAudioDataCallback.audioAacData(0,chunkAudio,chunkAudio.length);
                        //fos.write(chunkAudio);
                        mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                        outputBufferIndex = mMediaCodec.dequeueOutputBuffer(outBufferInfo, timeoutUs);
                    }
                }
            }
       // } finally {
            Log.i(TAG, "startAsync release");
            mAudioRecord.stop();
            mAudioRecord.release();
            mMediaCodec.stop();
            mMediaCodec.release();
       // }
    }

    public void stop() {
        Log.d(TAG, "stop() called");
        mStopped = true;
    }

    /**
     * Add ADTS header at the beginning of each and every AAC packet.
     * This is needed as MediaCodec encoder generates a packet of raw
     * AAC data.
     * <p>
     * Note the packetLen must count in the ADTS header itself.
     */

   // int profile = 2;  //AAC LC，MediaCodecInfo.CodecProfileLevel.AACObjectLC;
    //int freqIdx = 4;  //见后面注释avpriv_mpeg4audio_sample_rates中441000对应的数组下标，来自ffmpeg源码
    //        int chanCfg = 1;  //见后面注释channel_configuration，AudioFormat.CHANNEL_IN_MONO 单声道(声道数量)
   // int chanCfg = chancfg;  //见后面注释channel_configuration，AudioFormat.CHANNEL_IN_MONO 单声道(声道数量)

    /*int avpriv_mpeg4audio_sample_rates[] = {96000, 88200, 64000, 48000, 44100, 32000,24000, 22050, 16000, 12000, 11025, 8000, 7350};
    channel_configuration: 表示声道数chanCfg
    0: Defined in AOT Specifc Config
    1: 1 channel: front-center
    2: 2 channels: front-left, front-right
    3: 3 channels: front-center, front-left, front-right
    4: 4 channels: front-center, front-left, front-right, back-center
    5: 5 channels: front-center, front-left, front-right, back-left, back-right
    6: 6 channels: front-center, front-left, front-right, back-left, back-right, LFE-channel
    7: 8 channels: front-center, front-left, front-right, side-left, side-right, back-left, back-right, LFE-channel
    8-15: Reserved
    */

    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;  //AAC LC
        int freqIdx = 8;  //16K
        int chanCfg = 1;  //CPE
        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }
}
