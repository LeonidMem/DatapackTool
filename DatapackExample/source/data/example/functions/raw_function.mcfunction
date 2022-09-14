# Ignore this file on compilation, so there won't be 'example:raw_function' in compiled datapack
#$ ignore

# The first argument is "Single"
say %0

# The second argument is "word"
say %1

# The third argument is "or two and more, if there are quotes"
say %2

# \% needs to avoid replacement of this percentage symbol as extended function argument
# \% will be replaced to % after compilation
say \%3
