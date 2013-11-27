ifdef BUILD_PROJECT

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ScenarioDue
LOCAL_SRC_FILES := ScenarioDue.c
LOCAL_SHARED_LIBRARIES := gstreamer_android
LOCAL_LDLIBS := -llog -landroid
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := Incoming
LOCAL_SRC_FILES := Incoming.c
LOCAL_SHARED_LIBRARIES := gstreamer_android
LOCAL_LDLIBS := -llog -landroid
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := Audio
LOCAL_SRC_FILES := Audio.c
LOCAL_SHARED_LIBRARIES := gstreamer_android
LOCAL_LDLIBS := -llog -landroid
include $(BUILD_SHARED_LIBRARY)

ifndef GSTREAMER_SDK_ROOT
ifndef GSTREAMER_SDK_ROOT_ANDROID

endif
GSTREAMER_SDK_ROOT        := C:/gstreamer-sdk/0.10/x86_64/
endif
GSTREAMER_NDK_BUILD_PATH  := C:/gstreamer-sdk/0.10/x86_64/share/gst-android/ndk-build/
include C:/gstreamer-sdk/0.10/x86_64/share/gst-android/ndk-build/plugins.mk 
GSTREAMER_PLUGINS         := $(GSTREAMER_PLUGINS_CODECS_RESTRICTED) $(GSTREAMER_PLUGINS_CORE) $(GSTREAMER_PLUGINS_SYS) $(GSTREAMER_PLUGINS_EFFECTS) $(GSTREAMER_PLUGINS_NET) $(GSTREAMER_PLUGINS_CODECS) $(GSTREAMER_PLUGINS_PLAYBACK)
GSTREAMER_EXTRA_DEPS      := gstreamer-interfaces-0.10 gstreamer-video-0.10
include C:/gstreamer-sdk/0.10/x86_64/share/gst-android/ndk-build/gstreamer.mk

endif