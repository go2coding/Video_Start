
extern "C"
{
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include "libavutil/imgutils.h"
};

#include "decoder.h"

AVFrame* decode_to_rgb(AVFrame* frame) {
	// ����һ��swsContext������YUV��RGB��ת��
	SwsContext* swsContext = sws_getContext(frame->width, frame->height, (AVPixelFormat)frame->format,
		frame->width, frame->height, AV_PIX_FMT_RGB24,
		SWS_BILINEAR, NULL, NULL, NULL);
	if (!swsContext) {
		// ������...
	}

	// ����һ���µ�AVFrame�����ڴ洢RGB����
	AVFrame* rgbFrame = av_frame_alloc();
	rgbFrame->format = AV_PIX_FMT_RGB24;
	rgbFrame->width = frame->width;
	rgbFrame->height = frame->height;
	av_frame_get_buffer(rgbFrame, 0);

	// ��YUV����ת��ΪRGB
	sws_scale(swsContext, frame->data, frame->linesize, 0, frame->height,
		rgbFrame->data, rgbFrame->linesize);

	// �ͷ�swsContext
	sws_freeContext(swsContext);

	return rgbFrame;
}

bool yuv_to_jpeg(void* framev) {

	AVFrame* frame = (AVFrame*)framev;
	const AVCodec* jpegCodec = avcodec_find_encoder(AV_CODEC_ID_MJPEG);
	if (!jpegCodec) {
		return false;
	}
	AVCodecContext* jpegContext = avcodec_alloc_context3(jpegCodec);
	if (!jpegContext) {
		return false;
	}

	jpegContext->pix_fmt = AV_PIX_FMT_YUVJ420P;
	jpegContext->height = frame->height;
	jpegContext->width = frame->width;
	jpegContext->time_base.den = 20;
	jpegContext->time_base.num = 1;
	if (frame->height <= 0)return false;

	int ret = avcodec_open2(jpegContext, jpegCodec, NULL);
	if (ret < 0) {
		//char* ret =(char*) av_err2str(ret);
		return false;
	}

	AVPacket* packet;
	packet = av_packet_alloc();

	// ����֡��������
	if (avcodec_send_frame(jpegContext, frame) < 0) {
		// ������...
	}

	if (avcodec_receive_packet(jpegContext, packet) == 0) {
		// ��������������JPEG���ݣ����䱣�浽�ļ�
		FILE* JPEGFile;
		char JPEGFName[256];
		static int i = 0;
		sprintf(JPEGFName, "jpg//dvr-%06d.jpg", ++i);
		JPEGFile = fopen(JPEGFName, "wb");
		fwrite(packet->data, 1, packet->size, JPEGFile);
		fclose(JPEGFile);
	}


	av_packet_unref(packet);
	avcodec_close(jpegContext);
	return true;
}