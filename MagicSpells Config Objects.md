MagicSpells Config Objects
===========
This is a basic guide to the configuration representation of various objects in MagicSpells. By no means is this guaranteed to be complete but rather a quick reference on how to put together common details.

Vector
--------
The vector format is `x,y,z` where each letter may be replaced with a `double` value.

Prompt
--------
- `prompt-type` accepts a `string` and will fail if not set. Current valid values are
  - `regex`
  - `fixed-set`
  - `enum`

In addition to any additional options specified by the format of the specific prompt type.

RegexPrompt
--------
- Everything that MagicPromptResponder has
- `regexp` accepts a `string` and fails if not set to a valid regular expression.
- `prompt-text` accepts a `string` to use as the prompt's message to the player.

FixedSetPrompt
--------
- Everything that MagicPromptResponder has
- `options` accepts a `list` of strings and fails if not set.
- `prompt-text` accepts a `string` to use as the prompt's message to the player.

EnumSetPrompt
--------
- Everything that MagicPromptResponder has
- `enum-class` accepts a `string` of the class to load the value options from.
- `prompt-text` accepts a `string` to use as the prompt's message to the player.

MagicPromptResponder
--------
- `variable-name` accepts a `string` of the variable name to save the validated prompt response to.

ConversationFactory
--------
- `prefix` accepts a `string` and defaults to nothing.
- `local-echo` accepts a `boolean` and defaults to `true`.
- `first-prompt` accepts a `configuration section` in `prompt` format.
- `timeout-seconds` accepts an `integer` and defaults to `30`.
- `escape-sequence` accepts a `string` and defaults to nothing.
