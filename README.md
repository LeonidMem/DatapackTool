# DatapackTool
Very powerful tool for the datapacks' creators

## Example: [GitHub Wiki](https://github.com/LeonidMem/DatapackTool/wiki/Example)

# Brief Wiki

## Installation
**!!!** `DatapackTool` is compatible only with Windows **!!!**

Just download `DatapackTool.zip`, open or extract it and run `dtool-install.bat`. After that enter the directory, where DatapackTool will be located *(until 1.0.0 sometimes it works awfully with spaces even if it's all wrapped in the quotes, so be careful)*.

During the installation `explorer.exe` will be killed and opened again, so probably you won't need to restart the computer.

After this check, if DatapackTool is installed successfully, by writing command `dtool ?`. It should send more information, otherwise, restart the computer.

## Configuration

Before the project building, you must configure it. To do this, use command `dtool config set <project_id> <absolute_source_code_path> <absolute_out_code_path>`. If you have spaces in the path, just wrap it in the quotes like `"C:\Test Path\My Datapack"`.

And you must know that **DatapackTool may work incorrectly if you will set not the root folder of the datapack as a source code path**.

## Building

You wrote some code and want to build your project. What to do?

Just run `dtool build <project_id>` or `dtool build` *(this option will build the last built project)*. If there aren't any exceptions, it's done and you can test your datapack!

## Basic syntax

There are two types of commands in DatapackTool: parameters *(prefix **`#$`**)* and usual commands *(prefix **`#!`**)*.

The first one is used when you just specify some information, the other one when it edits your code directly. There is one more difference: commands can take `anonymous functions` as arguments, but parameters no.

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
* `#$ tag namespace:path/function_tag_name` will add the function, where this parameter appears, in the functions' tag with the given name.

### Built-in commands *(don't copy `<` and `>` from the commands below, they aren't needed)*:
* `#! execute <any Minecraft execute conditions> {anonymous function}` - wrap anonymous function in another file and call it from the `execute` command with given conditions.
* `#! set <variable_name_without_spaces> <value with any size and amount of spaces>` - set local variable.

**`[!]`** Local variables don't spread on anonymous functions and all built-in variables will be placed only while building the project, just know it.

* `#! set <variable_name_without_spaces>` - clear local variable.

* `#! globalset <variable_name_without_spaces> <value with any size and amount of spaces>` - set global variable *(doesn't work perfectly yet, but these variables will work in anonymous functions in any case)*.

* `#! globalset <variable_name_without_spaces>` - clear global variable.

* `%variable_name%` - get value from the variable with specified name if exists, otherwise, nothing changes. Can be used in any context.

## Modules

There is a modules system, so you can install any using only one command: `dtool module download [github_repository] <path_to_jar_file>`.

If you don't specify the GitHub repository, it will download the module from [the official page with official modules *(they are 100% safe)*](https://github.com/LeonidMem/DatapackTool-Modules).
