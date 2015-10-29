'''Code to test python being right.'''
import sys
for line in sys.stdin.readlines():
	print sum(map(int, line.split()))

