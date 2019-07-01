# Appranix CLI
# What is Appranix CLI? #
Appranix CLI is a simple command-line tool for users to deploy, update, operate and destroy their Applications.

# Why Appranix CLI? #
It provides a fast and repeatable way to deploy, operate and destroy applications in Appranix. It can be integrated with any application for convenience. The CLI tool is an addition to the Appranix UI interfaces.

# Installation
- [Hardware & Software Requirements](#hardware--software-requirements)
- [Install Appranix Command Line](#install-appranix-cli)
- [Configuration](#configuration)
- [External Internet Access](#external-internet-access)

### Hardware & Software Requirements
The following hardware and software requirements are used for the installation of Appranix CLI.

1. OS - CentOS 7 / Ubuntu 16.04+
2. Packages
  * Java 8 Oracle
3. Recommended hardware
  * Memory - 4GB
  * CPU - 2 cores
  * Storage - 20 GB (Root disk 10GB + Data disk 10GB)
  * Network - access to internet (NAT or proxy)

### Install Appranix Command Line

Download the executable jar as a binary file and change the file permissions.
```sh
$ curl -o ax http://tools.appranix.net/ax-cli/ax-cli-latest.jar
$ chmod +x ax
```
After setting the file permissions for ax, set the path of ax in $PATH.

Use below command for more information

```sh
$ ax --help
```

### Configuration
Appranix YAML templates are processed with Mustache to allow variable interpolation when the variable file contains a default profile. Appranix YAML templates can contain variables whose values are obtained from the variable file. The variable file can have variables, that is grouped in different profiles.

For example if we have the variable file as follows:

```sh
[default]
host=https://localhost:9090
organization=default
api_key=appranix_api_key # copy from Appranix UI->profile->authentication->API Token
email=user@appranix.com

[dev]
host=https://localhost:8090
organization=devorg
api_key=dev_api_key # copy from Appranix UI->profile->authentication->API Token
email=dev@appranix.com

```

The ax YAML template as follows:

```sh
version: 'ax/1-beta'

ax:
  appranix_host: '{{host}}'
  organization: '{{organization}}'
  api_key: '{{api_key}}'
  email: '{{email}}'
  environment_name: 'dev'
  io_format: 'json'
assembly:
  name: 'demo-assembly'
  auto_gen: false
  tags: {}
  description:

platforms: @include(yaml):path/to/platform/design.yml

environment:
  global_dns: true
  availability: 'single' # single | redundant
  envgroup: 'QA' # dev | QA | Production
  clouds:
    prod-c5c6: # cloud name
       priority: '1'
       deployment_order: '1'
       percent_scale: '100'
...
```

And we execute the following command to select the default profile.
```sh
$ ax --profile <PROFILE> --create -f <ax-yaml> --variable-file <ini file>
```

It will yield the following:
```sh
version: 'ax/1-beta'

ax:
  appranix_host: 'https://localhost:9090'
  organization: 'devtools'
  api_key: 'appranix_api_key'
  email: 'user@appranix.com'
  environment_name: 'dev'
  io_format: 'json'

assembly:
  name: 'demo-assembly'
  auto_gen: false
  tags: {}
  description:

platforms: @include(yaml):path/to/platform/design.yml

environment:
  global_dns: true
  availability: 'single' # single | redundant
  envgroup: 'QA' # dev | QA | Production
  clouds:
    prod-c5c6: # cloud name
       priority: '1'
       deployment_order: '1'
       percent_scale: '100'
...
```

The following code is a placeholder that replaces it with content from design.yml
```
@include(yaml):path/to/platform/design.yml
```

Sample platform design YAML is here:
```sh
platforms:
  web: # platform name
    pack: core/tomcat # pack name and platform name
    pack_version: '1'
    include: true # flag for include the platform
    components:
      app: # component
        display: # name of the component
          variables:
            key: value # component attributes

```

You can use variable file and ax YAML to create assembly, platforms and environments. It will be used to deploy their application using the following command:
```sh
$ ax --create -f ax.yml --variable-file ax-variables.ini
```
For an existing assembly, you can update your application and deploy it using the following command:
```sh
$ ax --update -f ax.yml --variable-file ax-variables.ini
```
Existing environments can also be configured using the above design YAML snippet.

For further clarification, contact support@appranix.com

### External Internet Access
All the connections will use either NAT or HTTP proxy if available in the operating system.
