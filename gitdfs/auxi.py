import commands
import os

def writeLines(fileName,line):
	f = open(fileName,'a')
	print >> f,line
	f.close()


def parse(info):
	commit_list = str.split(info,'\t')
	commit_head = str.split(commit_list[0],'\n')
	commit_temp = str.split(commit_head[0],' ')
	commit = commit_temp[1]
	return commit

def getLines(fileName):
	f = open(fileName,'r')
	restore_list = []
	for lines in f:
		l = len(lines)
		lines = lines[0:l-1]
		lines = str.split(lines,'\t')
		restore_list.append(lines)
	f.close()
	return restore_list

def showList(restore_list):
	i = 0
	for row in restore_list:
		assert len(row)==2
		print str(i)+'\t'+row[0]+'\t'+row[1]
		i = i+1
	 

def getCurrentPath(pit):
	return pit
	
def getCurrentDir(pit):
	path = getCurrentPath(pit)
	path_temp = str.split(path,'/')
	return path_temp[-1]

def check_remote(remote_git,remote_dir):
	if len(remote_git)==0 or len(remote_dir)==0:
		print "connection has not been completed or error occurred in connection"
		return 0
	else:
		return 1

def check_local(local_git,local_dir):
	if len(local_git)==0 or len(local_dir)==0:
		print "initialization has not been completed or error occurred in initialization"
		return 0
	else:
		return 1


def getRemoteGit(remote_git,remote_dir,pit):
	pos = remote_dir.index(pit)
	return remote_git[pos]
	
def cd_forward(dest,pit,last):
	last = pit
	pit = pit+'/'+dest
	return (pit,last)

def cd_back(dest,pit,last):
	cur_dir = getCurrentDir(pit)
	pit = last
	pos = last.find(cur_dir)
	last = last[0:pos-1]
	return(pit,last)

def write(objFile_whole):
	os.system('vim '+objFile_whole)

