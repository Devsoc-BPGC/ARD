You need to follow the below instructions carefully

0. You are not supposed to send PR of your forked repo's `master` branch
1. An issue that your PR is fixing should exist in the Issue section
2. Issue should have the "approved" label
3. In the first line in the description of your PR, include issue number as the first message. 
  eg. 
  `Issue #123`
4. Commit message should follow the following regexp patter
`^Issue #\d+: .$`
It must **not** end with a period, space, tab or a newline
valid egs. `Issue #123: This is my complete commit message`
invalid egs. 
`Issue #123: This is my complete commit message.` or
`This is my complete commit message where I forgot Issue reference` or
`Issue #123 Forgot colon in commit message` or
`Issue 123: This is my complete commit message but forgot hash symbol`

5. PR Title: Follow same pattern as commit message

Thanks for reading! You can remove this whole message now and type in your description.
