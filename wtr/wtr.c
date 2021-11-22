/*
	wtr - provides weather for the current location from wttr.in

	Copyright 2018 Jade Michael Thornton
	https://gitlab.com/thornjad/wtr

	Permission to use, copy, modify, and/or distribute this software for any
	purpose with or without fee is hereby granted, provided that the above
	copyright notice and this permission notice appear in all copies.

	THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
	REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
	AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
	INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
	LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
	OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
	PERFORMANCE OF THIS SOFTWARE.

*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <curl/curl.h>

#define MAX_LEN 50

int main(int argc, char *argv[]) {
	CURL *curl;
	CURLcode res;
	char baseUrl[] = "wttr.in/";
	char suffix[MAX_LEN] = {""};

	curl = curl_easy_init();

	if (curl) {
		if (argc > 1) {
			// concat arg list into one string
			char *buff = (char *)calloc(MAX_LEN + 1, 0);
			int offset = 0;
			while (argv++, --argc) {
				int toWrite = MAX_LEN - offset;
				int written = snprintf(buff + offset, toWrite, "%s ", *argv);
				if (toWrite < written) {
					break;
				}
				offset += written;
			}

			strcpy(suffix, curl_easy_escape(curl, buff, strlen(buff)));
			free(buff);
		}

		char *url = (char *)malloc(strlen(baseUrl) + strlen(suffix));
		if (url != NULL) {
			sprintf(url, "%s%s", baseUrl, suffix);

			curl_easy_setopt(curl, CURLOPT_URL, url);
			curl_easy_setopt(curl, CURLOPT_VERBOSE, 0L);
			curl_easy_setopt(curl, CURLOPT_USERAGENT, "curl");

			res = curl_easy_perform(curl);

			if (res != CURLE_OK) {
				fprintf(stderr, "failed to retrieve weather: %s\n", curl_easy_strerror(res));
			}

			free(url);
			curl_easy_cleanup(curl);
		} else {
			fprintf(stderr, "failed to allocate space");
			curl_easy_cleanup(curl);
			return 1;
		}
	}

	return 0;
}
