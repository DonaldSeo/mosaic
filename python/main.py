import files

# ../assets/file00053809264.jpg
target = raw_input('select target photo file:')
while (not files.isPhoto(target) ):
	target = raw_input('select valid photo file:')
	files.isPhoto(target)
# we got valid photo here

# ../assets/
photo_dir = raw_input('select target photos folder:')
while (not files.validFolder(photo_dir)):
	photo_dir = raw_input('select target photos folder:')
	files.validFolder(photo_dir)	
# we got valid folder with 30 or more jpgs