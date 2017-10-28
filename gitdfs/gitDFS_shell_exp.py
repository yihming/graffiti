#!/usr/bin/python

import commands
import init
import os
import auxi
import time


def shell():
	
	#--meta variable
	remote_dir = []
	remote_git = []
	
	local_dir = 'none'
	local_git = 'none'
	root_dir = os.environ['HOME']+'/gitDFS'
	pit = root_dir
	last = root_dir

	#--initialization
	print "Welcome to gitDFS~~~"
	local_name = raw_input('please type in the name of the local machine, skip this, type \'s \':')
	if local_name!='s':
		(local_dir,local_git) = init.local(local_name)
	else: 
		f = open(os.environ['HOME']+'/os_project/info/local_name','r')
		name = f.read()
		name = str.split(name,'\n')
		name = name[0]
		local_name = name
		print 'local_name is:'+local_name
		local_dir = root_dir+'/'+local_name
		local_git = root_dir+'/'+local_name+'.git'
		print 'local_dir is:'+local_dir+', local_git is:'+local_git
		print '==============================================================================='

	#--set test result file name
	filename = "test.dat"

	#--Open it
	FILE = open(filename, "a")

	#--cmd execution
	cmd = raw_input('gitDFS >>')
	while(cmd!='exit'):
		# set start time
		start_time = time.time()
		if cmd == 'connect':
			(remote_dir,remote_git) = init.connect()
			# calculate the used time
			end_time = time.time()
			time_used = end_time - start_time
			FILE.write("connect\t" + str(time_used) + "\n");

		elif cmd == 'ls -r':
			cur_dir = auxi.getCurrentDir(pit)
			if (pit.find('_remote')!=-1): #-- remote ls
				#print 'remote ls'
				if auxi.check_remote(remote_git,remote_dir):
					git_r = auxi.getRemoteGit(remote_git,remote_dir,pit)
					#(sta,info) = commands.getstatusoutput('git pull '+git_r)
					refresh(cur_dir,git_r)
					os.system('ls '+pit)
					# calculate the used time
					end_time = time.time()
					time_used = end_time - start_time
					#print "ls -r Time Used: %f" % (end_time - start_time)
					FILE.write("ls_r\t" + str(time_used) + "\n")
				else:
					print 'error_occurred_in '+cmd
			elif (pit.find('.git')!=-1): #-- git ls
				#print 'git ls'
				os.system('ls '+pit)
			else: #-- local ls
				#print 'local ls'
				if auxi.check_local(local_git,local_dir):
					#(sta,info) = commands.getstatusoutput('git pull '+local_git)
					refresh_local(local_name,local_git)
					os.system('ls '+pit)
					# calculate the used time
					end_time = time.time()
					time_used = end_time - start_time
					FILE.write("ls_r\t" + str(time_used) + "\n")
				else:
					print 'error occurred in '+cmd
		elif cmd == 'ls':
			os.system('ls '+pit)
			# calculate the time used
			end_time = time.time()
			time_used = end_time - start_time
			FILE.write("ls_l\t" + str(time_used) + "\n")
		elif cmd.find('cd')!=-1:
			dest = str.split(cmd,' ')
			dest = dest[-1]
			(sta,next) = commands.getstatusoutput('ls '+pit)
			if not (dest in next) and (dest != '..'):
				print dest+' does not exisit'
				
			elif not os.path.exists(pit+'/'+dest):
				print dest+' is not a directory'
				
			elif dest == '..':
				(pit,last) = auxi.cd_back(dest,pit,last)
			else:
				(pit,last) = auxi.cd_forward(dest,pit,last)
			# calculate the time used
			end_time = time.time()
			time_used = end_time - start_time
			FILE.write("cd\t" + str(time_used) + "\n")
		elif cmd == 'pwd':
			print pit
			# calculate the time used
			end_time = time.time()
			time_used = end_time - start_time
			FILE.write("pwd\t" + str(time_used) + "\n")

		elif cmd.count('read -r')==1:
			objFile = str.split(cmd,' ')
			objFile = objFile[-1]
			objFile_whole = pit+'/'+objFile
			if not os.path.isfile(objFile_whole):
				print 'file '+objFile+' does not exist'
				
			else:
				cur_dir = auxi.getCurrentDir(pit)
				if pit.find('_remote')!=-1: #-- remote read
					print 'remote read'
					git_r = auxi.getRemoteGit(remote_git,remote_dir,pit)
					refresh(cur_dir,git_r)
					os.system('chmod 555 '+objFile_whole)
					# calculate the used time
					end_time = time.time()
					time_used = end_time - start_time
					FILE.write("read_r\t" + str(time_used) + "\n")
					# get the file size
					#objfile_size = os.path.getsize(objFile_whole)
					#FILE.write("file_size:\t" + str(objfile_size) + "\n")

					os.system('vim '+objFile_whole)
				elif pit.find('.git')!=-1:  #-- git read
					os.system('chmod 555 '+ objFile_whole)
					# calculate the time used
					end_time = time.time()
					time_used = end_time - start_time
					FILE.write("read_r\t" + str(time_used) + "\n")

					os.system('vim '+objFile_whole)
				else: #-- local read
					#(sta,info) = commands.getstatusoutput('git pull '+local_git)
					refresh_local(local_name,local_git)
					os.system('chmod 555 '+ objFile_whole)
					# print the time info
					end_time = time.time()
					time_used = end_time - start_time
					FILE.write("read_r\t" + str(time_used) + "\n")
					# get the file size
					#objfile_size = os.path.getsize(objFile_whole)
					#FILE.write("file_size:\t" + str(objfile_size) + "\n")

					os.system('vim '+objFile_whole)

		elif cmd.count('read')==1 and cmd.count('read -r')==0:
			objFile = str.split(cmd,' ')
			objFile = objFile[-1]
			objFile_whole = pit+'/'+objFile
			if not os.path.isfile(objFile_whole):
				print 'file '+objFile+' does not exist'
				
			else:
				os.system('chmod 555 '+objFile_whole)
				os.system('vim '+objFile_whole)
			# calculate the time used
			end_time = time.time()
			time_used = end_time - start_time
			FILE.write("read\t" + str(time_used) + "\n")


			
		elif cmd.find('write')!=-1:
			objFile = str.split(cmd,' ')
			objFile = objFile[-1]
			objFile_whole = pit+'/'+objFile
			existed = 'T'
			newFile = 'OFF'
			if not os.path.isfile(objFile_whole):
				existed = 'F'
				print 'file '+objFile+' does not exist,do you want to create a new one? (y/n)'
				choose = raw_input()
				if choose == 'y':
					newFile = 'ON'
				elif choose == 'n':
					newFile = 'OFF'
				else:
					print 'neither y nor n, invalid input.'

			if existed == 'F' and newFile == 'OFF':
				print 'no file is created.'
			else:
				cur_dir = auxi.getCurrentDir(pit)
				if pit.find('_remote')!=-1:  #-- remote write
					print 'remote write'
					if cmd.count('write -r')==1:
						if existed == 'T':
							refresh(cur_dir)
						else:
							print 'the file '+objFile+' does not exist, you can\'t use command:'+cmd
							return
					if existed == 'F':
						f = open(objFile_whole,'w')
						f.close()

					os.system('chmod a+w+r '+objFile_whole)
					auxi.write(objFile_whole)
					msg = raw_input('please type in the modify message:')
					
					# set the beginning time
					start_time = time.time()

					git_r = auxi.getRemoteGit(remote_git,remote_dir,pit)
					update(msg,cur_dir,objFile,git_r,'m')

					# print the time info
					end_time = time.time()
					time_used = end_time - start_time
					FILE.write("write\t" + str(time_used) + "\n")
					# get the size of written file object
					#objfile_size = os.path.getsize(objFile_whole);
					#FILE.write("file_size:\t" + str(objfile_size) + "\n")					

				elif pit.find('.git')!=-1:  #-- git write, forbidden
					print 'you cannot modiy .git directory'
				else:  #-- local write
					print 'local write'
					if cmd.count('write -r')==1:
						if existed == 'T':
							refresh_local(local_name,local_git)
						else:
							print 'the file '+objFile+' does not exist, you can\'t use command:'+cmd
							return
					if existed == 'F':
						f = open(objFile_whole,'w')
						f.close()

					os.system('chmod a+w+r '+objFile_whole)
					os.system('vim '+objFile_whole)
					msg = raw_input('please type in the modify message:')

					# set the beginning time
					start_time = time.time()

					update_local(msg,local_name,objFile,local_git,'m')

					# print the time info
					end_time = time.time()
					time_used = end_time - start_time
					FILE.write("write\t" + str(time_used) + "\n")
					# get the size of written file object
					#objfile_size = os.path.getsize(objFile_whole)
					#FILE.write("file_size:\t" + str(objfile_size) + "\n")

		elif cmd == 'upload':
			cur_dir = auxi.getCurrentDir(pit)
			if pit.find('_remote')!=-1:  #-- remote directory
				git_r = auxi.getRemoteGit(remote_git,remote_dir,pit)
				upload(cur_dir,git_r)

				# calculate the used time
				end_time = time.time()
				time_used = end_time - start_time

				# print the time info
				FILE.write("upload\t" + str(time_used) + "\n")
			elif pit.find('_.git')!=-1:  #-- .git directory
				print 'nothing to upload in '+cur_dir
			else:   #-- local direcory
				upload_local(local_name,local_git)
				# calculate the used time
				end_time = time.time()
				time_used = end_time - start_time
				# print the time info
				FILE.write("upload\t" + str(time_used) + "\n")

		elif cmd.find('del')!= -1:
			objFile = str.split(cmd,' ')
			objFile = objFile[-1]
			objFile_whole = pit+'/'+objFile
			
			if not os.path.isfile(objFile_whole):
				print 'file '+objFile+' does not exist'
				
			else:
				cur_dir = auxi.getCurrentDir(pit)
				if pit.find('_remote')!=-1:  #--remote delete
					# get the size of deleted file object
					objfile_size = os.path.getsize(objFile_whole)

					os.system('rm '+objFile_whole)
					git_r = auxi.getRemoteGit(remote_git,remote_dir,pit)
					update(cmd,cur_dir,objFile,git_r,'not_m')
					# calculate the used time
					end_time = time.time()
					time_used = end_time - start_time
					# print the time and size info
					FILE.write("del\t" + str(time_used) + "\n")
					#FILE.write("file_size:\t" + str(objfile_size) + "\n")

				elif pit.find('.git')!=-1:   #--.git delete is not allowed
					print '.git delete is not allowed!'
				else:   #-- local delete
					# get the size of deleted file object
					objfile_size = os.path.getsize(objFile_whole)

					os.system('rm '+objFile_whole)
					update_local(cmd,local_name,objFile,local_git,'not_m')
					# calculate the used time
					end_time = time.time()
					time_used = end_time - start_time
					# print the time and size info
					FILE.write("del\t" + str(time_used) + "\n")
					#FILE.write("file_size:\t" + str(objfile_size) + "\n")

		
		elif cmd == 'revert':
 			cur_dir = auxi.getCurrentDir(pit)
			if pit.find('_remote')!=-1:   #-- remote directory
				print cmd+' is not allowed in '+cur_dir
			elif pit.find('.git')!=-1:  #-- .git directory
				print cmd+' is not allowed in '+cur_dir
			else:  #-- local directory
				#--rename master branch				
				exe_rename = 'git --git-dir=../gitDFS/'+local_name+'/.git --work-tree=../gitDFS/'+local_name+' branch -m master old_m'
				print 'exe_rename:'+exe_rename
				(sta,info) = commands.getstatusoutput(exe_rename)
				print info
				#--create new branch master
				exe_newb = 'git --git-dir=../gitDFS/'+local_name+'/.git --work-tree=../gitDFS/'+local_name+' checkout -b master HEAD^'
				print 'exe_newb:'+exe_newb				
				(sta1,info1) = commands.getstatusoutput(exe_newb)
				print info1
				#--del old_m master
				exe_del = 'git --git-dir=../gitDFS/'+local_name+'/.git --work-tree=../gitDFS/'+local_name+' branch -D old_m'
				print 'exe_del:'+exe_del
				(sta2,info2) = commands.getstatusoutput(exe_del)
				print info2
				#--syn .git diretory
				revert_git = 'git --git-dir=../gitDFS/'+local_name+'.git --work-tree=../gitDFS/'+local_name+'.git update-ref HEAD HEAD^'

				# calculate the used time
				end_time = time.time()
				time_used = end_time - start_time
				FILE.write("revert\t" + str(time_used) + "\n")

				print 'syn .git:'+revert_git
				(sta3,info3) = commands.getstatusoutput(revert_git)
				print info3
			


		elif cmd == 'restore':
			cur_dir = auxi.getCurrentDir(pit)
			#-----------------choose the right file and get the restore_list-----------------
			if pit.find('_remote')!=-1:
				print cmd+' is not allowed in '+cur_dir
				return
			elif pit.find('.git')!=-1:
				print cmd+' is not allowed in '+cur_dir
				return
			else:
				fileName = os.environ['HOME']+'/os_project/info/restore_local'
				restore_list = auxi.getLines(fileName)
			
			#----------------------------handles empty files---------------------------------------------
			if len(restore_list)==0:
				print 'no restore points stored, use revert or revert -u to go back to the last version'
				return
			else:
				auxi.showList(restore_list)
				point_index = raw_input('choose the restore point you want:(row index starts from 0)')
				point = restore_list[int(point_index)]
				point = point[1]
			
			# set the start time
			start_time = time.time()
			

			#----------------------------begin to restore the chosen restore point--------------------------
			if pit.find('_remote')!=-1:  #--remote
				print cmd+' is not allowed in '+cur_dir
				return
			elif pit.find('.git')!=-1:  #--.git 
				print cmd+' is not allowed in '+cur_dir
				return
			else:  #--local
				#--rename master branch				
				exe_rename = 'git --git-dir=../gitDFS/'+local_name+'/.git --work-tree=../gitDFS/'+local_name+' branch -m master old_m'
				print 'exe_rename:'+exe_rename
				(sta,info) = commands.getstatusoutput(exe_rename)
				print info
				#--create new branch master
				exe_newb = 'git --git-dir=../gitDFS/'+local_name+'/.git --work-tree=../gitDFS/'+local_name+' checkout -f -b master '+point
				print 'exe_newb:'+exe_newb				
				(sta1,info1) = commands.getstatusoutput(exe_newb)
				print info1
				#--del old_m master
				exe_del = 'git --git-dir=../gitDFS/'+local_name+'/.git --work-tree=../gitDFS/'+local_name+' branch -D old_m'
				print 'exe_del:'+exe_del
				(sta2,info2) = commands.getstatusoutput(exe_del)
				print info2
				#--syn .git diretory
				revert_git = 'git --git-dir=../gitDFS/'+local_name+'.git --work-tree=../gitDFS/'+local_name+'.git update-ref HEAD '+point

				# calculate the used time
				end_time = time.time()
				time_used = end_time - start_time
				FILE.write("restore\t" + str(time_used) + "\n")

				print 'syn .git:'+revert_git
				(sta3,info3) = commands.getstatusoutput(revert_git)
				print info3

					
		elif cmd == 'backup':
			print '....................................system backup....................................'
			cur_dir = auxi.getCurrentDir(pit)
			if pit.find('_remote')!=-1:   #--remote
				print cmd+' is not allowed in '+cur_dir
			elif pit.find('.git')!=-1:  #--git
				print cmd+' is not allowed in '+cur_dir
			else:  #--local
				fileName = os.environ['HOME']+'/os_project/info/restore_local'
				exe_backup = 'git --git-dir=../gitDFS/'+local_name+'/.git log'
				print 'exe_backup:'+exe_backup
				(sta,info) = commands.getstatusoutput(exe_backup)
				print info
				commit = auxi.parse(info)
				print 'commit:'+commit
				# calculate the used time
				end_time = time.time()
				time_used = end_time - start_time
				FILE.write("backup\t" + str(time_used) + "\n")

				name = raw_input('pleas type in the name for the restore point:...')
				line = name+'\t'+commit
				auxi.writeLines(fileName,line)
		cmd = raw_input('gitDFS >> ')
	print "exit the gitDFS shell"
	FILE.close()

#-- functions for shell

def upload(cur_dir,git_r):
	exe_push = 'git --git-dir=../gitDFS/'+cur_dir+'/.git --work-tree=../gitDFS/'+cur_dir+' push '+git_r+' master'
	print 'upload exe_push:'+exe_push
	(sta2,info2) = commands.getstatusoutput(exe_push)
	print info2


def upload_local(local_name,local_git):
	exe_push = 'git --git-dir=../gitDFS/'+local_name+'/.git --work-tree=../gitDFS/'+local_name+' push '+local_git+' HEAD'
	print 'local upload local exe_push:'+exe_push
	(sta2,info2) = commands.getstatusoutput(exe_push)
	print info2

def refresh(cur_dir,git_r):
	exe_fetch = 'git --git-dir=../gitDFS/'+cur_dir+'/.git fetch'
	print 'fetch:'+exe_fetch
	(sta,info) = commands.getstatusoutput(exe_fetch)
	print info
	exe_merge = 'git --git-dir=../gitDFS/'+cur_dir+'/.git --work-tree=../gitDFS/'+cur_dir+' merge origin/master'
	print 'merge:'+exe_merge
	(sta1,info1) = commands.getstatusoutput(exe_merge)
	print info1

def refresh_local(local_name,local_git):
	exe_fetch = 'git --git-dir=../gitDFS/'+local_name+'/.git fetch '+local_git
	print 'local fetch:'+exe_fetch
	(sta,info) = commands.getstatusoutput(exe_fetch)
	print info
	exe_merge = 'git --git-dir=../gitDFS/'+local_name+'/.git --work-tree=../gitDFS/'+local_name+' merge FETCH_HEAD'
	print 'local merge:'+exe_merge
	(sta1,info1) = commands.getstatusoutput(exe_merge)
	print info1

def update(msg,cur_dir,objFile,git_r,choose):
	if choose == 'm':
		#--modify, need to add the file 
		exe_add = 'git --git-dir=../gitDFS/'+cur_dir+'/.git --work-tree=../gitDFS/'+cur_dir+' add '+objFile
		print 'update exe_add:'+exe_add
		(sta,info) = commands.getstatusoutput(exe_add)
		print info
 	#--else,delete operation or upload operation,no need to add the file
	exe_commit = 'git --git-dir=../gitDFS/'+cur_dir+'/.git --work-tree=../gitDFS/'+cur_dir+' commit -a -m '+'\''+msg+'\''
	print 'update exe_commit:'+exe_commit
	(sta1,info1) = commands.getstatusoutput(exe_commit)
	print info1
	exe_push = 'git --git-dir=../gitDFS/'+cur_dir+'/.git --work-tree=../gitDFS/'+cur_dir+' push '+git_r+' master'
	print 'update exe_push:'+exe_push
	(sta2,info2) = commands.getstatusoutput(exe_push)
	print info2

def update_local(msg,local_name,objFile,local_git,choose):
	if choose == 'm':
		#--modify,need to add the file
		exe_add = 'git --git-dir=../gitDFS/'+local_name+'/.git --work-tree=../gitDFS/'+local_name+' add '+objFile
		print 'local update exe_add:'+exe_add
		(sta,info) = commands.getstatusoutput(exe_add)
		print info
	#--else,delete operation or upload operation,no need to add the file
	exe_commit = 'git --git-dir=../gitDFS/'+local_name+'/.git --work-tree=../gitDFS/'+local_name+' commit -a -m '+'\''+msg+'\''
	print 'local update exe_commit:'+exe_commit
	(sta1,info1) = commands.getstatusoutput(exe_commit)
	print info1
	exe_push = 'git --git-dir=../gitDFS/'+local_name+'/.git --work-tree=../gitDFS/'+local_name+' push '+local_git+' HEAD'
	print 'local update exe_push:'+exe_push
	(sta2,info2) = commands.getstatusoutput(exe_push)
	print info2

if __name__ == '__main__':
	shell()
