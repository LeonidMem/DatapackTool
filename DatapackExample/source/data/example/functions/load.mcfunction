# Add this function to the "minecraft:load" functions' tag
#$ tag minecraft:load

# Set variables
#! set message Thanks for installing! This datapack was made with DTool!
#! set color green

# Don't use % in "set" command, because it may be replaced with the value of this variable
#! set %color% red

tellraw @a {"text":"%message%","color":"%color%"}
# By the way, all variables are case insensitive, so you can use it like here:
tellraw @a {"text":"Current version: %version%","color":"%COLOR%"}

# If you didn't set the variable, the compiler won't change anything
say %unset_variable%

scoreboard objectives add example.timer dummy

# "Extended function" feature: you can

#% function example:raw_function Single word "or two and more, if there are quotes"

# "#%" means that next symbols should be uncommented
# It's useful when you use Visual Studio Code with extension for datapacks development and
# you don't want to see red error highlights
#% give @a %steel_sword%
