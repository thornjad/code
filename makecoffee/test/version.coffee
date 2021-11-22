require('./setup')

describe 'Version', ->
  it 'should exist', ->
    version = require('../package.json').version
    expect(makecoffee.version).eql version

  it 'should be semver-like', ->
    expect(makecoffee.version).match /^\d+\.\d+\.\d+/
