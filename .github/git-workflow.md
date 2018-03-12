1. Don't make changes to `master` branch.

2. When you see a new issue you can solve, add a comment `on it` to the issue. Let's say the issue number is `N`.

3. Get latest upstream branch

  ```bash
  git checkout master
  git fetch upstream
  ```

  followed by

  ```bash
  git merge upstream/master
  ```

  **OR** if you failed at 1

  ```bash
  git reset --hard upstream/master
  ```

4. Create a new branch

  ```bash
  git checkout -b fix-issue-N
  ```

5. Do your thing. Commit.

6. _Squash_ all your commits into one. (Assuming you've done `M` commits for this patch) -

  ```bash
  git rebase -i HEAD~M
  ```

  In the next file that opens, replace all `pick` words except first one by `squash`. (If there is only `pick`, change it to `reword`). Save the file.<br>
  In the next file, write a commit message for your PR. (Following the PR format). Save the file.

7. Get latest commits from upstream -

  ```bash
  git checkout master
  git fetch upstream
  git merge upstream/master
  git checkout fix-issue-N
  git rebase -i master
  ```

  If you see any conflicts, resolve them.

8. Ensure that your branch is just one commit ahead of `upstream/master`.

  ```bash
  git log
  ```

9. Do `git diff master` to inspect your patch.

10. Push your patch to your fork -

  ```bash
  git push origin fix-issue-N -f
  ```

11. In github, start a pull request. If there are any suggestions, repeat from 5.

12. When your pr is accepted, delete the branch.

  ```bash
  git checkout master
  git branch -D fix-issue-N   # deletes the local fix-issue-N branch
  git push -d origin fix-issue-N     # deletes the fix-issue-N branch at origin
  ```
