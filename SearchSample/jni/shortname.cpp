/**
Copyright (c) 2011, KENSUKE NAKAI
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list
  of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this
  list of conditions and the following disclaimer in the documentation and/or
  other materials provided with the distribution.
* Neither the name of the nakaikensuke.com nor the names of its contributors may
  be used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
OF SUCH DAMAGE.
*/
#include <jni.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <sys/stat.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <linux/dirent.h>
#include <android/log.h>

#include "com_yohpapa_research_searchsample_ShortnameHelper.h"

#define VFAT_IOCTL_READDIR_BOTH  _IOR('r', 1, struct dirent [2])
#define VFAT_IOCTL_READDIR_SHORT  _IOR('r', 2, struct dirent [2])

static int openFile(JNIEnv* env, jobject thiz, jstring longPath) {
	const char* path = env->GetStringUTFChars(longPath, NULL);
	if(path == NULL)
		return 0;

	__android_log_print(ANDROID_LOG_DEBUG, "SHORTNAME", "open %s", path);
	int fd = open(path, O_RDONLY);

	env->ReleaseStringUTFChars(longPath, path);

	return fd;
}


jbyteArray Java_com_yohpapa_research_searchsample_ShortnameHelper_GetShortName(
	JNIEnv* env,
	jclass	thiz,
	jstring dirName,
	jstring fileName) {

	const char* dir = env->GetStringUTFChars(dirName, NULL);
	if(dir == NULL) {
		__android_log_print(ANDROID_LOG_DEBUG, "SHORTNAME", "env->GetStringUTFChars failed 1");
		return NULL;
	}

	const char* file = env->GetStringUTFChars(fileName, NULL);
	if(file == NULL) {
		__android_log_print(ANDROID_LOG_DEBUG, "SHORTNAME", "env->GetStringUTFChars failed 2");
		env->ReleaseStringUTFChars(dirName, dir);
		return NULL;
	}

	jbyteArray shortName = NULL;

	// ファイルオープン
	int fd = open(dir, O_RDONLY);
	if(fd == 0) {
		__android_log_print(ANDROID_LOG_DEBUG, "SHORTNAME", "open failed");
		env->ReleaseStringUTFChars(dirName, dir);
		env->ReleaseStringUTFChars(fileName, file);
		return NULL;
	}

	// ioctlを使ってショートネーム取得
	while(1) {

		struct dirent entries[2] = {0};
		int ret = ioctl(fd, VFAT_IOCTL_READDIR_BOTH, &entries);
		if(ret == -1) {
			__android_log_print(ANDROID_LOG_DEBUG, "SHORTNAME", "ioctl failed 1");
			break;
		}

		if(entries[0].d_reclen == 0) {
			__android_log_print(ANDROID_LOG_DEBUG, "SHORTNAME", "ioctl failed 2");
			continue;
		}

		const char* d_name = entries[0].d_name;
		if(entries[1].d_reclen != 0) {
			d_name = entries[1].d_name;
		}

		// ロングネームが一致しているかチェック
		if(strcmp(file, d_name) != 0) {
			continue;
		}

		// 取得したショートネームをjbyteArrayオブジェクトに格納
		shortName = env->NewByteArray(entries[0].d_reclen);
		jbyte* elements = env->GetByteArrayElements(shortName, NULL);

		for(int i = 0; i < entries[0].d_reclen; i ++) {
			elements[i] = entries[0].d_name[i];
		}

		env->ReleaseByteArrayElements(shortName, elements, 0);
		break;
	}
	
	// 最後にファイルクローズ
	close(fd);

	env->ReleaseStringUTFChars(dirName, dir);
	env->ReleaseStringUTFChars(fileName, file);

	return shortName;
}
