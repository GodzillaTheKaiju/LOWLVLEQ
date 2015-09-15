#ifndef Header_myFilters
#define Header_myFilters

#include <SLES/OpenSLES.h>
//#include "OpenSLES.h"
#include <SLES/OpenSLES_Android.h>
//#include "OpenSLES_Android.h"
#include <math.h>
#include <pthread.h>
//#include "pthread.h"

#include "myFilters.h"
#include "SuperpoweredAdvancedAudioPlayer.h"
#include "SuperpoweredFilter.h"
#include "SuperpoweredMixer.h"
#include "myFilters.h"

#define NUM_BUFFERS 2
#define HEADROOM_DECIBEL 3.0f
static const float headroom = powf(10.0f, -HEADROOM_DECIBEL * 0.025);

class myFilters {
public:
	

	myFilters(const char *path);
	~myFilters();
	jfloatArray shortToByte(jshortArray input, jint input_length);
	void process(jfloatArray input);
	
	void setFilt(int vol, int filt, float q);
	

private:
	SLAndroidSimpleBufferQueueItf bufferQueue;
	SuperpoweredFilter *filt1;
	SuperpoweredFilter *filt2;
	SuperpoweredFilter *filt3;
	SuperpoweredFilter *filt4;
	SuperpoweredFilter *filt5;
	SuperpoweredFilter *filt6;
	SuperpoweredFilter *filt7;
	SuperpoweredFilter *filt8;
	SuperpoweredStereoMixer *mixer;
	pthread_mutex_t mutex;
	int FREQS[8];
	jbyte* inBuffer;
	float *outputBuffer[NUM_BUFFERS];
	float max, min;
	int currentBuffer, buffersize;

};



#endif // !Header_myFilters
