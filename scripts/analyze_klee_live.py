#!/usr/bin/env python3
import sys
import re
import time
error_regex = re.compile(r'error_([0-9]+)')

start_time = time.time()
error_dict = {}

verbose = True

try:
    for line in sys.stdin:
        if verbose:
            print(line, end='')
        for error in error_regex.findall(line):
            error = int(error)
            if error not in error_dict:
                dt = time.time() - start_time
                error_dict[error]= dt
                print(f'[info] Found new error: error_{error} in {dt}')
except KeyboardInterrupt:
    sys.stdout.flush()
    pass

print('\nSummary:')
for error, t in sorted(error_dict.items()):
    print(f'error_{error} found in {t:.3f}s')
print(f'Found {len(error_dict)} unique errors')
