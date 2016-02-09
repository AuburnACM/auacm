""" The reloader that autodetects file changes for debugging

Since we ditched the old werkzeug server, this is the code that allowed it to
refresh automatically. (Ripped from https://github.com/mitsuhiko/werkzeug/blob/1a21e27a99331d854cec9174a1fc0fcd9717a673/werkzeug/_reloader.py)

"""

import sys
import os
import subprocess
import time
from itertools import chain
import threading

def _iter_module_files():
    """This iterates over all relevant Python files.  It goes through all
    loaded files from modules, all files in folders of already loaded modules
    as well as all files reachable through a package.
    """
    # The list call is necessary on Python 3 in case the module
    # dictionary modifies during iteration.
    for module in list(sys.modules.values()):
        if module is None:
            continue
        filename = getattr(module, '__file__', None)
        if filename:
            old = None
            while not os.path.isfile(filename):
                old = filename
                filename = os.path.dirname(filename)
                if filename == old:
                    break
            else:
                if filename[-4:] in ('.pyc', '.pyo'):
                    filename = filename[:-1]
                yield filename

class ReloaderLoop(object):
    name = None

    # monkeypatched by testsuite. wrapping with `staticmethod` is required in
    # case time.sleep has been replaced by a non-c function (e.g. by
    # `eventlet.monkey_patch`) before we get here
    _sleep = staticmethod(time.sleep)

    def __init__(self, extra_files=None, interval=1):
        self.extra_files = set(os.path.abspath(x)
                               for x in extra_files or ())
        self.interval = interval

    def run(self):
        pass

    def restart_with_reloader(self):
        """Spawn a new Python interpreter with the same arguments as this one,
        but running the reloader thread.
        """
        while 1:
            print(' * Restarting with %s' % self.name)
            args = [sys.executable] + sys.argv
            new_environ = os.environ.copy()
            new_environ['WERKZEUG_RUN_MAIN'] = 'true'

            exit_code = subprocess.call(args, env=new_environ,
                                        close_fds=False)
            if exit_code != 3:
                return exit_code

    def trigger_reload(self, filename):
        self.log_reload(filename)
        sys.exit(3)

    def log_reload(self, filename):
        filename = os.path.abspath(filename)
        print(' * Detected change in %r, reloading' % filename)

class StatReloaderLoop(ReloaderLoop):
    name = 'stat'

    def run(self):
        mtimes = {}
        while 1:
            for filename in chain(_iter_module_files(),
                                  self.extra_files):
                try:
                    mtime = os.stat(filename).st_mtime
                except OSError:
                    continue

                old_time = mtimes.get(filename)
                if old_time is None:
                    mtimes[filename] = mtime
                    continue
                elif mtime > old_time:
                    self.trigger_reload(filename)
            self._sleep(self.interval)

def run_with_reloader(main_func, extra_files=None, interval=1,
                      reloader_type='auto'):
    """Run the given function in an independent python interpreter."""
    import signal
    reloader = StatReloaderLoop(extra_files, interval)
    signal.signal(signal.SIGTERM, lambda *args: sys.exit(0))
    new_environ = os.environ.copy()
    new_environ['WERKZEUG_RUN_MAIN'] = 'true'
    try:
        if os.environ.get('WERKZEUG_RUN_MAIN') == 'true':
            t = threading.Thread(target=main_func, args=())
            t.setDaemon(True)
            t.start()
            reloader.run()
        else:
            sys.exit(reloader.restart_with_reloader())
    except KeyboardInterrupt:
        pass
