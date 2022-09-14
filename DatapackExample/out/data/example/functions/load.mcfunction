tellraw @a {"text":"Thanks for installing! This datapack was made with DTool!","color":"green"}
tellraw @a {"text":"Current version: 1.0.0","color":"green"}
say %unset_variable%
scoreboard objectives add example.timer dummy
function example:load-1709482948
give @a netherite_sword{display:{Name:'{"text":"Steel Sword","color":"red"}'},Enchantments:[{id:"sharpness",lvl:10}]}
