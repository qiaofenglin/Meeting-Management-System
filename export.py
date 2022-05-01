#!/usr/bin/python3.7  
# -*- coding: utf-8 -*-  
from urllib3 import *
from re import *
import re
import os
import hashlib
import requests
from bs4 import BeautifulSoup
import bs4

def walkFile(file):

    for root, dirs, files in os.walk(file):

        # root 表示当前正在访问的文件夹路径

        # dirs 表示该文件夹下的子目录名list

        # files 表示该文件夹下的文件list

        # 遍历文件

        for f in files:
            if ".java" in f or ".css" in f or ".fxml" in f:
                print(os.path.join(root, f))
                with open(os.path.join(root, f)) as f1: #读取每个文件
                    flag=0
                    for line in f1.readlines(): #将每个文件文本统一逐行写入一个word中
                        with open("code.doc","a") as mom:
                            if flag==0:
                                mom.write('\n\n\n'+f)	#每行开头写入文件名
                                flag=1
                            mom.write('\n'+line)

walkFile("src")

