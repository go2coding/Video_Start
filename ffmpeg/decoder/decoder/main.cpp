#define SDL_MAIN_HANDLED 1

#include <stdio.h>
#include "SDL2/SDL.h"

extern "C"
{
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include "libavutil/imgutils.h"
};

#include "decoder.h"




int main(int argc, char* argv[])
{

	
	//视频格式，准备转码

	char filepath[] = "bigbuckbunny_480x272.h265";

	AVFormatContext* pFormatCtx;
	int				i, videoindex;
	const AVCodec* pCodec;

	
	pFormatCtx = avformat_alloc_context();

	if (avformat_open_input(&pFormatCtx, filepath, NULL, NULL) != 0) {
		printf("Couldn't open input stream.\n");
		return -1;
	}
	if (avformat_find_stream_info(pFormatCtx, NULL) < 0) {
		printf("Couldn't find stream information.\n");
		return -1;
	}

	videoindex = -1;
	for (i = 0; i < pFormatCtx->nb_streams; i++)
		if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
			videoindex = i;
			break;
		}
	if (videoindex == -1) {
		printf("Didn't find a video stream.\n");
		return -1;
	}

	pCodec = avcodec_find_decoder(pFormatCtx->streams[videoindex]->codecpar->codec_id);

	if (pCodec == NULL) {
		printf("Codec not found.\n");
		return -1;
	}
	//创建解码的上下文
	AVCodecContext* codecContext = avcodec_alloc_context3(pCodec);
	avcodec_parameters_to_context(codecContext, pFormatCtx->streams[videoindex]->codecpar);
	if (avcodec_open2(codecContext, pCodec, NULL) < 0) {
		printf("Could not open codec.\n");
		return -1;
	}

	//SDL2
	if (SDL_Init(SDL_INIT_VIDEO)) {
		//printf( "Could not initialize SDL - %s\n", SDL_GetError()); 
		return -1;
	}

	int screen_w = codecContext->width;
	int screen_h = codecContext->height;

	SDL_Window* screen;
	SDL_Renderer* sdlRenderer;
	SDL_Texture* sdlTexture;
	SDL_Rect sdlRect;

	//SDL 2.0 Support for multiple windows
	screen = SDL_CreateWindow("ffmpeg decoder", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED,
		screen_w, screen_h,
		SDL_WINDOW_OPENGL);

	if (!screen) {
		printf("SDL: could not create window - exiting:%s\n",SDL_GetError());  
		return -1;
	}

	sdlRenderer = SDL_CreateRenderer(screen, -1, 0);
	sdlTexture = SDL_CreateTexture(sdlRenderer, SDL_PIXELFORMAT_IYUV, SDL_TEXTUREACCESS_STREAMING, screen_w, screen_h);

	//显示rgb
	//sdlTexture = SDL_CreateTexture(sdlRenderer, SDL_PIXELFORMAT_RGB24, SDL_TEXTUREACCESS_STREAMING, screen_w, screen_h);

	sdlRect.x = 0;
	sdlRect.y = 0;
	sdlRect.w = screen_w;
	sdlRect.h = screen_h;

	//读取文件并解码

	AVPacket packet;
	AVFrame* frame = av_frame_alloc();
	SDL_Event event;
	bool running = true;
	
	while (av_read_frame(pFormatCtx, &packet) >= 0 && running) {

		//窗口可移动
		while (SDL_PollEvent(&event)) {
			if (event.type == SDL_QUIT) {
				running = false;
			}
		}

		//H264转为yuv
		if (packet.stream_index == videoindex) {
			if (avcodec_send_packet(codecContext, &packet) < 0) {
				// 错误处理...
			}
			while (avcodec_receive_frame(codecContext, frame) == 0) {
				// 此时，frame包含YUV数据

				//把他存储为图片
				yuv_to_jpeg(frame);

				//灰色显示
				//memset(frame->data[1], 128, frame->linesize[1] * frame->height / 2);
				//memset(frame->data[2], 128, frame->linesize[2] * frame->height / 2);


				//显示yuv
				SDL_UpdateYUVTexture(sdlTexture, &sdlRect,
					frame->data[0], frame->linesize[0],
					frame->data[1], frame->linesize[1],
					frame->data[2], frame->linesize[2]);

				//显示rgb
				/*AVFrame* rgbFrame = decode_to_rgb(frame);
				SDL_UpdateTexture(sdlTexture, NULL, rgbFrame->data[0], rgbFrame->linesize[0]);
				av_frame_free(&rgbFrame);*/



				SDL_RenderClear(sdlRenderer);
				SDL_RenderCopy(sdlRenderer, sdlTexture, NULL, &sdlRect);
				SDL_RenderPresent(sdlRenderer);
				//SDL End-----------------------
				//Delay 1000/60ms--假设每分钟60帧
				SDL_Delay(1000/60);
			}
			av_packet_unref(&packet);
		}
		
	}

	// 释放资源
	av_frame_free(&frame);
	avcodec_close(codecContext);
	avcodec_free_context(&codecContext);
	avformat_close_input(&pFormatCtx);

	return 0;
}