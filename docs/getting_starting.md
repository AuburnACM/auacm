# Start the Codes!

So after getting the project setup (if you haven't, check out the installation [here](https://github.com/AuburnACM/auacm/blob/master/docs/installation.md)) what's next? Here are a few useful things to know before getting started on the site!

## IDE Setup

To write some sweet code, it's important to have the right tools for the job.
This is why we recommend using Microsoft's [Visual Studio Code](https://code.visualstudio.com/) for development on the site.
This simple, lightweight IDE is extremely useful for working with web projects, including Typescript.
If you decide to use VSC, here are a list of useful extensions for the IDE:

1. Auto-Open Markdown Preview

2. Python (includes a linters, debugging, and code completion)

Also you will want to enable VSC to start using a terminal. To do this, open VSC, type ⇧⌘P (Mac) or ctrl+⇧P (linux) to open the command tab, and enter `shell command`. You should see this:

![Shell Command](https://code.visualstudio.com/images/mac_shell-command.png)

After slecting `install 'code' command in PATH`, you will be able to open projects straight from the terminal using `code project/folder`.

## Useful Commands

In the main directory of the project, we have a Makefile with a few useful commands for building and linting the project. Here are the most important ones:

* `make` - make by itself will build the front-end in dev mode. This will bundle all of the Typescript together into a few files, but will not minimize it. This makes debugging your code inside your browser. However, you will need to hard reload the page so that your browser fetches the files again from the server.

* `make prod` - makes the production version of the front-end. It will bundle all of the typescript components and dependencies together into a few files, then minify it. This will shorten variable names, remove unused dependencies, and create files with a hashed name.

* `make lint` - this will run the angular-cli linter and the Python linter pylint. Your code should pass both of the linters before you create a pull request.

The next commands are Angular specific (front-end only.) This means you should execute these commands inside the `/../auacm/angular/` folder.

* `ng generate [name]` - allows you to create a new Angular template. Here is a list of templates you can generate:

   * `class`
   * `component`
   * `directive`
   * `enum`
   * `guard`
   * `interface`
   * `module`
   * `pipe`
   * `service`

* `npm install -s <package-name>` - installs a npm package and adds the package information to the package.json file.

* `npm uninstall -s <package-name>` - removes a npm package and removes the package information from the package.json file.
