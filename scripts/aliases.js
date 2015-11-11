var Texts = Packages.org.spongepowered.api.text.Texts;
var CommandSpec = Packages.org.spongepowered.api.util.command.spec.CommandSpec;

function alias(from, to, options) {
    var realOpts = options || {};
    var builder = CommandSpec.builder()
        .description(Texts.of(realOpts.description || ("Alias for command " + to)))
        .args(Packages.org.spongepowered.api.util.command.arg.GenericArguments.remainingJoinedStrings(Texts.of("args")))
        .executor(function (src, args) {
            var joinedArgs = args.getOne("args").get();
            if (typeof(to) === "string") { // Accepts either a single string or a JS array/iterable/whatever
                game.getCommandService().process(src, to);
            } else {
                for each (var cmd in to) {
                    game.getCommandService().process(src, cmd);
                }
            }
        });

    if (realOpts.permission) {
        builder.permission(realOpts.permission);
    }

    ctx.onCommand(builder.build(), to)
}

//exports.alias = alias; // TODO: Make this mean something

alias("gm", "gamemode");
alias("cleanup", ["weather clear", "time set day"]);