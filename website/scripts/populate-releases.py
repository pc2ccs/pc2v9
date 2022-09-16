#!/usr/bin/env python3
import requests
import os
import re
import json
import datetime
import itertools
import sys

destination = "website/data/releases/"
public_url = 'https://api.github.com/repos/pc2ccs/builds/releases'
nightly_url = 'https://api.github.com/repos/pc2ccs/nightly-builds/releases'
included_versions = {}

def all_releases(url, token):
    result = []

    linkPattern = re.compile(r'<(?P<url>.*)>; rel="(?P<type>.*)"')

    r = request_from_github(url, token)
    result += r.json()
    while "Link" in r.headers:
        links = r.headers["Link"].split(", ")
        nextFound = False
        for link in links:
            match = linkPattern.search(link)
            if match.group("type") == "next":
                r = request_from_github(match.group("url"), token)
                result += r.json()
                nextFound = True
        if not nextFound:
            break

    return sorted(result, key=lambda release: release["published_at"], reverse=True)

def request_from_github(url, token):
    if token == None:
        token = os.environ["GITHUB_TOKEN"]
    headers = {
        'Accept': 'application/vnd.github.v3+json',
        'Authorization': 'token {}'.format(token)
    }
    r = requests.get(url, headers=headers)

    return r

def release_info(release):
    assetPattern = re.compile(r'(?P<tool>.*)-(?P<version>\d+\.\d+(\.\d+|build)-\d+(.develop)?).(zip|tar\.gz)\.?(?P<check>.*)?')
    # restore the 9.6.0 release date to '2019-01-02T03:34:47Z'
    if release['published_at'] == '2020-05-02T02:08:37Z':
       release['published_at'] =  '2019-01-02T03:34:47Z'
    # restore the 9.5.4 release date to '2018-09-26T04:39:38Z'
    if release['published_at'] == '2020-05-02T02:05:22Z':
       release['published_at'] =  '2018-09-26T04:39:38Z'
    # restore the 9.4.4 release date to '2017-01-24T02:58:32Z'
    if release['published_at'] == '2020-05-02T02:02:05Z':
       release['published_at'] =  '2017-01-24T02:58:32Z'
    # restore the 9.3.4 release date to '2017-01-24T02:52:49Z'
    if release['published_at'] == '2020-05-02T01:59:13Z':
       release['published_at'] =  '2017-01-24T02:52:49Z'
    # restore the 9.2.5 release date to '2015-10-14T04:00:38Z'
    if release['published_at'] == '2020-05-02T01:54:50Z':
       release['published_at'] =  '2015-10-14T04:00:38Z'
    # restore the 9.1.7 release date to '2014-09-22T00:40:10Z'
    if release['published_at'] == '2020-05-02T01:53:08Z':
       release['published_at'] =  '2014-09-22T00:40:10Z'
    info = {
        'version': release['tag_name'].replace('v', ''),
        'published_at':  release['published_at'],
        'date': datetime.datetime.strptime(release['published_at'], "%Y-%m-%dT%H:%M:%SZ").strftime("%d %B %Y"),
        'time': datetime.datetime.strptime(release['published_at'], "%Y-%m-%dT%H:%M:%SZ").strftime("%H:%M:%S"),
        'downloads': {}
    }

    for asset in release["assets"]:
        match = assetPattern.search(asset['name'])
        if not match:
           print("ERROR: " + asset['name'] + " failed to match " + assetPattern + "\n")
           next
        tool = match.group('tool')
        version = match.group('version')
        check = match.group('check')
        url = asset['browser_download_url']
        if not tool in info['downloads']:
            info['downloads'][tool] = {
                'version': version,
                'urls': {'sha256': {}, 'sha512': {}},
                'sizes': {},
            }
        if check == "":
            if 'zip' in url:
                info['downloads'][tool]['urls']['zip'] = url
                info['downloads'][tool]['sizes']['zip'] = round(asset['size'] / 1024 / 1024, 2)
            else:
                info['downloads'][tool]['urls']['tar_gz'] = url
                info['downloads'][tool]['sizes']['tar_gz'] = round(asset['size'] / 1024 / 1024, 2)
        else:
            if ".txt" in check:
                check = check[:-4]
            if 'zip' in url:
                info['downloads'][tool]['urls'][check]['zip'] = url
            else:
                info['downloads'][tool]['urls'][check]['tar_gz'] = url

    return info

def base_tag_name(tag_name):
   return tag_name[:tag_name.rindex('.')]

def filter_tag_name(release):
   tag_name = base_tag_name(release['tag_name'])
   if tag_name in included_versions:
      return False
   else:
      included_versions[tag_name]="1"
      return True

if not os.path.isdir(destination): 
    os.mkdir(destination, 0o755)

token = None
if len(sys.argv) > 1:
    token = sys.argv[1]
releases = all_releases(public_url, token)
nightly_releases = all_releases(nightly_url, token)

latest_stable = list(filter(lambda release: not release["prerelease"], releases))[0]
included_versions[base_tag_name(latest_stable['tag_name'])]="1"
all_stable = list(filter(lambda release: not release["prerelease"], releases))
prev_stables = list(filter(filter_tag_name, all_stable))
list_latest_prerelease = list(filter(lambda release: release["prerelease"], releases))
if list_latest_prerelease:
   latest_prerelease = list_latest_prerelease[0]
else:
   latest_prerelease = latest_stable
latest_nightly = list(filter(lambda release: release["prerelease"], nightly_releases))[0]

all_releases = [*releases, *nightly_releases]
all_releases_sorted = sorted(all_releases, key=lambda all_releases: all_releases["published_at"], reverse=True)
all_releases_list = list(map(lambda release: release_info(release), all_releases_sorted))
all_releases_sorted = sorted(all_releases_list, key=lambda release: release["published_at"], reverse=True)
prev_stables = sorted(prev_stables, key=lambda releases: releases["published_at"], reverse=True)
prev_stables_list = list(map(lambda release: release_info(release), prev_stables))


files = {
    'stable': json.dumps(release_info(latest_stable), indent=4, sort_keys=True),
    'prerelease': json.dumps(release_info(latest_prerelease), indent=4, sort_keys=True),
    'nightly': json.dumps(release_info(latest_nightly), indent=4, sort_keys=True),
    'all': json.dumps(all_releases_sorted, indent=4, sort_keys=True),
    'prev_stable': json.dumps(prev_stables_list, indent=4, sort_keys=True),
}

for file in files:
    f = open(destination + file + '.json', 'w')
    if files[file] != '""':
      f.write(files[file])
    f.close()
    print("Wrote " + file + " to " + destination + file + '.json')
