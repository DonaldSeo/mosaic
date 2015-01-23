from os import listdir
from os.path import isfile, join, isdir

def isPhoto(target):
	extention = target.split('.')[-1] == 'jpg'
	if not extention:
		return extention
	return isfile(target)

def listFiles(loc):
	files = [ f for f in listdir(loc) if isfile(join(loc,f)) ]
	return files

# true if folder has photos.jpg 
def validFolder(loc):
	# add trailing slash if not set
	if not isdir(loc):
		return False

	if (loc[-1] != '/'):
		loc += '/'
	lst = listFiles(loc)
	if (len(lst) < 30):
		return False

	for x in lst:
		if x == 'dl.txt':
			break
		x = loc+x
		if not isPhoto(x):
			return False
	return True


# validFolder('../assets')