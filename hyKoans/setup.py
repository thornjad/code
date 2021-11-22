#!/usr/bin/env python

from setuptools import setup, find_packages

def readme():
	try:
		return open('README.md').read()
	except:
		pass
	return ''

setup(
	name='hykoan',
	version='0.1.0',
	install_requires=['hy>=0.16.0'],
	author="Jade Michael Thornton",
	description="Get Hy by fixing these tests!",
	long_description=readme(),
	license="MIT",
	url='http://github.com/taddeimania/hykoan',
	packages=find_packages(),
	test_suite="nose.collector",
	entry_points={
		'console_scripts': [
			'hyk=hykoan.cmdline:main',
		]
	},
	platforms=['any']
)
