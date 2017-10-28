#include <iostream>
#include <cstdio>
#include <fstream>
#include <string>
using namespace std;

float time_used = 0.0;
float file_size = 0.0;
float const_file[8] = {26389, 58849280, 1826, 4147454, 7293522, 496181, 4689511, 923387};

void init() {
	int i;
	for (i = 0; i < 8; ++i) {
		file_size += const_file[i];
	}
}

float calc_rate(char* filename, int num) {
    ifstream testfile (filename);
    float current_time;
    float current_filesize;
    char   tmp_str1[100], tmp_str2[100];
    string content;
    float result = 0.0;
    int i;

    for (i = 0; i < num; ++i) {
	init();
    }

    cout << "Opening " << filename << "..." << endl;

    if (testfile.is_open()) {
        cout << "Open success!" << endl;

        cout << "Calculating results..." << endl;
        while (getline(testfile, content)) {
            sscanf(content.c_str(), "%s\t%f\t%s\t%f", tmp_str1, &current_time, tmp_str2, &current_filesize);
            time_used += current_time;
            file_size += current_filesize;
        }
        result = file_size / (time_used * 1024);
        cout << "Calculating Finished!" << endl;
	cout << "Total data size is: " << (file_size / (1024 * 1024)) << " MB." << endl;
	cout << "Total time used is: " << time_used << " seconds." << endl;
    } else {
        cout << "Cannot open " << filename << "!" << endl;
    }

    testfile.close();

    return result;
}

void calc_latency(char* filename, string cmd) {
    ifstream testfile(filename);
    float result = 0.0;
    int count = 0;
    string content;
    char cur_cmd[50];
    float cur_latency;
    float max_latency = 0.0;
    float min_latency = 100000.0;

    cout << "Opening " << filename << "..." << endl;

    if (testfile.is_open()) {
        cout << "Open success!" << endl;

        cout << "Calculating results..." << endl;
        while (getline(testfile, content)) {
            sscanf(content.c_str(), "%s\t%f", cur_cmd, &cur_latency);

            string cur_cmd_str(cur_cmd);

            if (cur_cmd_str == cmd) {
                result += cur_latency;
                ++count;

                if (max_latency < cur_latency)
                    max_latency = cur_latency;

                if (min_latency > cur_latency)
                    min_latency = cur_latency;
            }

        }

    } else {
        cout << "Cannot open " << filename << "!" << endl;
    }

    // Calculate the result.
    result = result / count;

    cout << "Statistics of " << cmd << ":" << endl;
    cout << "---- Average Latency: " << result << " seconds" << endl;
    cout << "---- Maximum Latency: " << max_latency << " seconds" << endl;
    cout << "---- Minimum Latency: " << min_latency << " seconds" << endl;
    cout << "---- Frequency: " << count << endl;
    cout << endl;

}

int main(int argc, char* argv[]) {

    string option(argv[1]);

    if (option.substr(0, 2) == "-c") {
	char c = option.at(2);
	int num = c - '0';
	cout << "We used " << num << " dataset(s)." << endl;

	float transfer_rate = calc_rate(argv[2], num);

	cout << "The transfer rate is: " << transfer_rate << " KB / s." << endl;
    } else if (option.substr(0, 2) == "-m") {
		// Get the statistics of the specific command.
		string cmd(argv[2]);

        string filename(argv[3]);


		calc_latency(argv[3], cmd);


    } else  {
        cout << "Unknow Option" << endl;
    }


    return 0;
}
