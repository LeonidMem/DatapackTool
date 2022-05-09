# Add this function to the "minecraft:tick" functions' tag
#$ tag minecraft:tick

# Creating the timer, so all heavy calculations will be called once in two seconds
scoreboard players add *timer example.timer 1

# Just common execute, but more readable and optimized
#! execute if score *timer example.timer matches 40.. { 

    scoreboard players set *timer example.timer 0

    #! execute as @e[tag=!example.checked] {

        say I am a new mob here!

        #! execute if entity @s[type=zombie] {

            item replace entity @s armor.head with white_stained_glass
            item replace entity @s armor.chest with iron_chestplate
            item replace entity @s armor.legs with iron_leggings
            item replace entity @s armor.feet with iron_boots

            #% item replace entity @s weapon.mainhand with %steel_sword%

        #! }

        #! execute if entity @s[type=skeleton] {

            item replace entity @s armor.head with yellow_stained_glass
            item replace entity @s armor.chest with golden_chestplate
            item replace entity @s armor.legs with golden_leggings
            item replace entity @s armor.feet with golden_boots

        #! }

        #! execute if entity @s[type=pig] {

            attribute @s generic.movement_speed base set 1
            attribute @s generic.max_health base set 2

        #! }

    #! }

#! }
