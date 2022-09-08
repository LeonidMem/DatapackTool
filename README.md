# DatapackTool
Very powerful compiler for the datapacks' creators with datapack-like language.

## Example: [GitHub Wiki](https://github.com/LeonidMem/DatapackTool/wiki/Example)

# Brief Wiki

## Installation
**!!!** `DatapackTool` is compatible only with Windows **!!!**

Just download `DatapackTool.zip`, open or extract it and run `dtool-install.bat`. After that enter the directory, where 
DatapackTool will be located *(until 1.0.0 sometimes it works awfully with spaces even if it's all wrapped in the quotes, 
so be careful)*.

During the installation `explorer.exe` will be killed and opened again, so probably you won't need to restart the computer.

After this check, if DatapackTool is installed successfully, by writing command `dtool ?`. It should send more information, 
otherwise, restart the computer.

## Setup

Before the project building, you must setup **DTool's environment**. To do this, use command `dtool env init`.

After this two files fill be created: **`dtool/build.json`** and **`dtool/init.mcfunction`**.

In the first file you can configure build: set **`out`** directory, where will be stored compiled code, **`args`**, 
so you don't need to write it every build, and `modules`.

Also you must know that **DatapackTool may work incorrectly if you will set not the root folder of the datapack as 
a source code path**.

## Building

You wrote some code and want to build your project. What to do?

Just run or `dtool build` in the direcrtory, where **DTool's environment was setup**. If there aren't any exceptions, 
it's done and you can test your datapack!

## Basic syntax

There are two types of commands in DatapackTool: parameters *(prefix **`#$`**)* and usual commands *(prefix **`#!`**)*.

The first one is used when you just specify some information, the other one when it edits your code directly. There is 
one more difference: commands can take `anonymous functions` as arguments, but parameters no.

`Anonymous function` is a function, which will be moved in another in the out code. Example:

**Input**: `SourceDirectory/data/test/functions/test.mcfunction`:
```
#! execute as @e[type=zombie] {
    say 1
    say 2
    say 3
#! }
```

**Output**: `OutDirectory/data/test/functions/test.mcfunction` and `OutDirectory/data/test/functions/test1196742791.mcfunction`:
```
execute as @e[type=zombie] run function test:test1196742791
```

```
say 1
say 2
say 3
```

In this case, `test1196742791` is the `anonymous function`. Isn't difficult, yeah?


### Built-in parameters:
* `#$ tag namespace:path/function_tag_name` will add the function, where this parameter appears, in the functions' tag 
with the given name.

### Built-in commands *(don't copy `<` and `>` from the commands below, they aren't needed)*:
* `#! execute <any Minecraft execute conditions> {anonymous function}` - wrap anonymous function in another file and call 
  it from the `execute` command with given conditions.
* `#! set <variable_name_without_spaces> <value with any size and amount of spaces>` - set local variable.

**`[!]`** Local variables don't spread on anonymous functions and all built-in variables will be placed only while 
building the project, just know it.

* `#! set <variable_name_without_spaces>` - clear local variable.

* `#! globalset <variable_name_without_spaces> <value with any size and amount of spaces>` - set global variable *(it 
  must be used in `dtool/init.mcfunction`, or it won't work perfectly)*.

* `#! globalset <variable_name_without_spaces>` - clear global variable.

* `#! var <player> <scoreboard> <+/-/=><value>` - alias for `scoreboard players <depends on the math sign from 3rd arg> 
  <player> <scoreboard> <value>`

* `#! var <player1> <scoreboard1> <minecraft operation sign> <player2> <scoreboard2>` - alias for
  `scoreboard players operation <player1> <scoreboard1> <operation sign> <player2> <scoreboard2>`

* `%variable_name` - get value from the variable with specified name if exists, otherwise, nothing changes.
Can be used in any context.

## Other features
* Extended function. Now you can set arguments for the functions. Finally.

**Source code:**

**`data/example/functions/load.mcfunction`**:
```
#% function example:raw_function Single word "or two and more, if there are quotes"
```

**`data/example/functions/raw_function.mcfunction`**:
```
# The first argument is "Single"
say %0

# The second argument is "word"
say %1

# The third argument is "or two and more, if there are quotes"
say %2

# \% needs to avoid replacement of this percentage symbol as extended function argument
# \% will be replaced to % after compilation
say \%3
```

**Compiled code:**

**`data/example/functions/load.mcfunction`**:
```
function example:load342665618
```

**`data/example/functions/load342665618.mcfunction`**:
```
say Single
say word
say or two and more, if there are quotes
say %3
```

* You can use `#%` in the start of the command, and "#%" will be removed on compilation.

It's useful when you use Visual Studio Code with extension for datapacks development and you don't want to see 
red error highlights.

## Modules

There is a modules system, so you can install any using only one command: `dtool module download [github_repository] 
<path_to_jar_file>`.

If you don't specify the GitHub repository, it will download the module from
[the official page with official modules *(they are 100% safe)*](https://github.com/LeonidMem/DatapackTool-Modules).

After installation, you must update **`build.json`** and change value of the **`modules`** field.

For example, you installed `CoreM` module. So, now **`build.json`** must look like:
```json
{
    "out": "your/out/directory",
    "args": ["your", "args"],
    
    "modules": ["CoreM"]
}
```
