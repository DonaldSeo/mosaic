import files


target = raw_input('select target photo file:')
while (not files.isPhoto(target) ):
	target = raw_input('select valid photo file:')
	files.isPhoto(target)
# we got valid photo here

photo_dir = raw_input('select target photos folder:')
while (not files.validFolder(photo_dir))