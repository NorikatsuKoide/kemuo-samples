LOCAL_PATH	:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE	:= shortname
LOCAL_SRC_FILES	:= shortname.cpp
LOCAL_LDLIBS	:= -llog
LOCAL_ARM_MODE	:= arm

include $(BUILD_SHARED_LIBRARY)
