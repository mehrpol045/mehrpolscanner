package ui

import (
	"bytes"
	"net"
	"strings"
)

type irRange struct {
	from net.IP
	to   net.IP
	isp  string
}

var irRanges []irRange

const rawIRRanges = `
2.144.0.0	2.147.255.255	Iran Cell Service and Communication Company
2.176.0.0	2.191.255.255	Information Technology Company (ITC)
5.22.0.0	5.22.127.255	Mobile Communication Company of Iran PLC
5.34.192.0	5.34.207.255	Fanap - Passargad Arian Information & communication Tehchnology
5.52.0.0	5.52.255.255	Mobile Communication Company of Iran PLC
5.53.32.0	5.53.63.255	Iran Telecommunication Company PJS
5.62.160.0	5.62.191.255	Iran Telecommunication Company PJS
5.62.192.0	5.62.255.255	Rightel Communication Service Company PJS
5.72.0.0	5.72.255.255	Rightel Communication Service Company PJS
5.73.0.0	5.73.255.255	Rightel Communication Service Company PJS
5.74.0.0	5.74.255.255	Iran Telecommunication Company PJS
5.75.0.0	5.75.127.255	Iran Telecommunication Company PJS
5.106.0.0	5.106.255.255	Mobile Communication Company of Iran PLC
5.112.0.0	5.127.255.255	Iran Cell Service and Communication Company
5.134.128.0	5.134.191.255	Rightel Communication Service Company PJS
5.160.0.0	5.160.255.255	Respina Networks & Beyond PJSC
5.190.0.0	5.190.255.255	Iran Telecommunication Company PJS
5.198.160.0	5.198.191.255	Iran Telecommunication Company PJS
5.200.64.0	5.200.95.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
5.200.96.0	5.200.127.255	Rightel Communication Service Company PJS
5.200.128.0	5.200.255.255	Iran Telecommunication Company PJS
5.201.128.0	5.201.191.255	Mobin Net Communication Company (Private Joint Stock)
5.201.192.0	5.201.255.255	Mobile Communication Company of Iran PLC
5.202.0.0	5.202.255.255	Pishgaman Toseeh Ertebatat Company (Private Joint Stock)
5.208.0.0	5.209.255.255	Mobile Communication Company of Iran PLC
5.210.0.0	5.210.255.255	Mobile Communication Company of Iran PLC
5.211.0.0	5.211.255.255	Mobile Communication Company of Iran PLC
5.212.0.0	5.213.255.255	Mobile Communication Company of Iran PLC
5.214.0.0	5.215.255.255	Mobile Communication Company of Iran PLC
5.216.0.0	5.217.255.255	Mobile Communication Company of Iran PLC
5.218.0.0	5.218.255.255	Mobile Communication Company of Iran PLC
5.219.0.0	5.219.63.255	Iran Telecommunication Company PJS
5.219.64.0	5.219.127.255	Iran Telecommunication Company PJS
5.219.128.0	5.219.191.255	Iran Telecommunication Company PJS
5.219.192.0	5.219.255.255	Iran Telecommunication Company PJS
5.220.0.0	5.223.255.255	GOSTARESH-E-ERTEBATAT-E MABNA COMPANY (Private Joint Stock)
5.232.0.0	5.239.255.255	Iran Telecommunication Company PJS
5.250.0.0	5.250.127.255	Mobile Communication Company of Iran PLC
31.2.128.0	31.2.255.255	Mobile Communication Company of Iran PLC
31.7.64.0	31.7.79.255	Asiatech Data Transmission company
31.7.96.0	31.7.127.255	Iran Telecommunication Company PJS
31.7.128.0	31.7.143.255	Aryan Satellite Co. (Private Joint Stock)
31.14.80.0	31.14.95.255	Iran Telecommunication Company PJS
31.14.112.0	31.14.127.255	Afranet
31.14.144.0	31.14.159.255	Iran Telecommunication Company PJS
31.47.32.0	31.47.63.255	Afranet
31.56.0.0	31.59.255.255	Aria Shatel Company Ltd
31.130.176.0	31.130.191.255	05/05/11
31.170.48.0	31.170.63.255	Farahoosh Dena PLC
31.184.128.0	31.184.191.255	GOSTARESH-E-ERTEBATAT-E MABNA COMPANY (Private Joint Stock)
37.19.80.0	37.19.95.255	Farabord Dadeh Haye Iranian Co.
37.32.0.0	37.32.31.255	Noyan Abr Arvan Co. ( Private Joint Stock)
37.32.112.0	37.32.127.255	Rayaneh Pardazan Baran Co. Ltd.
37.63.128.0	37.63.255.255	Mobile Communication Company of Iran PLC
37.98.0.0	37.98.127.255	Mobile Communication Company of Iran PLC
37.114.192.0	37.114.255.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
37.129.0.0	37.129.255.255	Mobile Communication Company of Iran PLC
37.137.0.0	37.137.255.255	Rightel Communication Service Company PJS
37.148.0.0	37.148.127.255	Aria Shatel Company Ltd
37.152.160.0	37.152.175.255	Rahanet Zanjan Co. (Private Joint-Stock)
37.152.176.0	37.152.191.255	Noyan Abr Arvan Co. ( Private Joint Stock)
37.153.176.0	37.153.191.255	Rightel Communication Service Company PJS
37.156.16.0	37.156.31.255	Mobin Net Communication Company (Private Joint Stock)
37.156.48.0	37.156.63.255	Rightel Communication Service Company PJS
37.156.112.0	37.156.127.255	Iran Telecommunication Company PJS
37.156.128.0	37.156.143.255	Iran Telecommunication Company PJS
37.156.144.0	37.156.159.255	Iran Telecommunication Company PJS
37.191.64.0	37.191.95.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
37.202.128.0	37.202.255.255	Aria Shatel Company Ltd
37.221.0.0	37.221.63.255	Farabord Dadeh Haye Iranian Co.
37.235.16.0	37.235.31.255	Farabord Dadeh Haye Iranian Co.
37.254.0.0	37.255.255.255	Iran Telecommunication Company PJS
46.21.80.0	46.21.95.255	ANDISHE SABZ KHAZAR CO. P.J.S.
46.32.0.0	46.32.31.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
46.34.96.0	46.34.127.255	Mellat Insurance Public Joint Stock Company
46.34.176.0	46.34.191.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
46.36.96.0	46.36.111.255	Pirooz Leen" LLC
46.38.128.0	46.38.159.255	Farhang Azma Communications Company LTD
46.41.192.0	46.41.255.255	Pars Online PJS
46.51.0.0	46.51.127.255	Mobile Communication Company of Iran PLC
46.100.0.0	46.100.255.255	Iran Telecommunication Company PJS
46.102.128.0	46.102.143.255	Afranet
46.143.0.0	46.143.127.255	Asiatech Data Transmission company
46.143.192.0	46.143.255.255	MIHAN COMMUNICATION SYSTEMS CO.,LTD
46.148.32.0	46.148.47.255	03/11/10
46.164.64.0	46.164.127.255	Mobile Communication Company of Iran PLC
46.167.128.0	46.167.159.255	Rayaneh Danesh Golestan Complex P.J.S. Co.
46.209.0.0	46.209.255.255	Respina Networks & Beyond PJSC
46.224.0.0	46.225.255.255	Rayaneh Danesh Golestan Complex P.J.S. Co.
46.245.0.0	46.245.63.255	Hamara System Tabriz Engineering Company
46.245.64.0	46.245.127.255	Asiatech Data Transmission company
46.248.32.0	46.248.63.255	Iran Telecommunication Company PJS
62.60.128.0	62.60.255.255	Iranian Research Organization for Science & Technology
62.102.128.0	62.102.143.255	Mobile Communication Company of Iran PLC
62.193.0.0	62.193.31.255	DP IRAN PLC
62.220.96.0	62.220.127.255	Soroush Rasanheh Company Ltd
66.79.96.0	66.79.127.255	Iran Telecommunication Company PJS
69.194.64.0	69.194.127.255	Mobile Communication Company of Iran PLC
77.36.128.0	77.36.255.255	IRIB (Islamic Republic of Iran Broadcasting)
77.77.64.0	77.77.127.255	Rayaneh Danesh Golestan Complex P.J.S. Co.
77.81.32.0	77.81.47.255	Iran Telecommunication Company PJS
77.81.144.0	77.81.159.255	Iran Telecommunication Company PJS
77.81.192.0	77.81.223.255	Rightel Communication Service Company PJS
77.104.64.0	77.104.127.255	Respina Networks & Beyond PJSC
77.237.64.0	77.237.95.255	Respina Networks & Beyond PJSC
77.237.160.0	77.237.191.255	Pishgaman Toseeh Ertebatat Company (Private Joint Stock)
77.245.224.0	77.245.239.255	Research Institute Of Petroleum Industry
78.38.0.0	78.39.255.255	Information Technology Company (ITC)
78.109.192.0	78.109.207.255	Afranet
78.154.32.0	78.154.63.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
78.157.32.0	78.157.63.255	Dade Samane Fanava Company (PJS)
78.158.160.0	78.158.191.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
79.127.0.0	79.127.127.255	Asiatech Data Transmission company
79.132.208.0	79.132.223.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
79.175.128.0	79.175.191.255	Afranet
80.66.176.0	80.66.191.255	University of Tehran
80.71.112.0	80.71.127.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
80.75.0.0	80.75.15.255	Afranet
80.191.0.0	80.191.255.255	Information Technology Company (ITC)
80.210.0.0	80.210.63.255	Iran Telecommunication Company PJS
80.210.128.0	80.210.255.255	Iran Telecommunication Company PJS
80.242.0.0	80.242.15.255	Mobile Communication Company of Iran PLC
80.250.192.0	80.250.207.255	Iran Telecommunication Company PJS
80.253.128.0	80.253.143.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
80.253.144.0	80.253.159.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
81.12.0.0	81.12.127.255	Soroush Rasanheh Company Ltd
81.16.112.0	81.16.127.255	Iran Telecommunication Company PJS
81.28.32.0	81.28.47.255	IsIran
81.28.48.0	81.28.63.255	IsIran
81.29.240.0	81.29.255.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
81.31.160.0	81.31.175.255	Sharif University Of Technology
81.31.176.0	81.31.191.255	Sharif University Of Technology
81.31.224.0	81.31.255.255	Chapar Rasaneh LLC
81.90.144.0	81.90.159.255	Afranet
81.91.128.0	81.91.143.255	Datak Company LLC
81.91.144.0	81.91.159.255	Farabord Dadeh Haye Iranian Co.
82.99.192.0	82.99.255.255	Pars Online PJS
82.180.192.0	82.180.255.255	Mobile Communication Company of Iran PLC
83.120.0.0	83.123.255.255	Mobile Communication Company of Iran PLC
83.147.192.0	83.147.255.255	Raya Sepehr Vira Data Processing Company Ltd.
84.47.192.0	84.47.223.255	Iran Telecommunication Company PJS
84.47.240.0	84.47.255.255	Iran Telecommunication Company PJS
84.241.0.0	84.241.63.255	Aria Shatel Company Ltd
85.9.64.0	85.9.127.255	Pishgaman Toseeh Ertebatat Company (Private Joint Stock)
85.15.0.0	85.15.63.255	Aria Shatel Company Ltd
85.133.128.0	85.133.255.255	Sepanta Communication Development Co. Ltd
85.185.0.0	85.185.255.255	Information Technology Company (ITC)
85.198.48.0	85.198.63.255	Sefroyek Pardaz Engineering PJSC
85.204.80.0	85.204.95.255	Iran Telecommunication Company PJS
85.204.208.0	85.204.223.255	Iran Telecommunication Company PJS
85.239.192.0	85.239.223.255	Mobile Communication Company of Iran PLC
86.55.0.0	86.55.255.255	Mobile Communication Company of Iran PLC
86.57.0.0	86.57.127.255	Sefroyek Pardaz Engineering PJSC
86.104.32.0	86.104.47.255	Afranet
86.104.80.0	86.104.95.255	Iran Telecommunication Company PJS
86.104.96.0	86.104.111.255	Iran Telecommunication Company PJS
86.105.128.0	86.105.143.255	Iran Telecommunication Company PJS
86.107.0.0	86.107.15.255	Mobile Communication Company of Iran PLC
86.107.80.0	86.107.95.255	Iran Telecommunication Company PJS
86.107.144.0	86.107.159.255	Iran Telecommunication Company PJS
86.107.208.0	86.107.223.255	Mobile Communication Company of Iran PLC
86.109.32.0	86.109.63.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
87.107.0.0	87.107.255.255	Soroush Rasanheh Company Ltd
87.248.128.0	87.248.159.255	TOSE'EH ERTEBATAT NOVIN ARIA CO PJS
87.251.128.0	87.251.159.255	Iran Telecommunication Company PJS
89.32.0.0	89.32.31.255	Rightel Communication Service Company PJS
89.32.96.0	89.32.111.255	Rightel Communication Service Company PJS
89.34.32.0	89.34.63.255	Rightel Communication Service Company PJS
89.34.128.0	89.34.159.255	Rightel Communication Service Company PJS
89.36.48.0	89.36.63.255	Iran Telecommunication Company PJS
89.36.96.0	89.36.111.255	Iran Telecommunication Company PJS
89.36.176.0	89.36.191.255	Iran Telecommunication Company PJS
89.37.0.0	89.37.15.255	Mobin Net Communication Company (Private Joint Stock)
89.37.240.0	89.37.255.255	Iran Telecommunication Company PJS
89.38.80.0	89.38.95.255	Iran Telecommunication Company PJS
89.40.240.0	89.40.255.255	Iran Telecommunication Company PJS
89.41.192.0	89.41.223.255	Rightel Communication Service Company PJS
89.43.0.0	89.43.15.255	Mobin Net Communication Company (Private Joint Stock)
89.45.48.0	89.45.63.255	Mobile Communication Company of Iran PLC
89.47.64.0	89.47.79.255	Rightel Communication Service Company PJS
89.47.128.0	89.47.159.255	Rightel Communication Service Company PJS
89.144.128.0	89.144.191.255	ANDISHE SABZ KHAZAR CO. P.J.S.
89.165.0.0	89.165.127.255	Parvaresh Dadeha Co. Private Joint Stock
89.196.0.0	89.196.255.255	Mobile Communication Company of Iran PLC
89.198.0.0	89.198.127.255	Mobile Communication Company of Iran PLC
89.198.128.0	89.198.255.255	Mobile Communication Company of Iran PLC
89.199.0.0	89.199.255.255	Mobile Communication Company of Iran PLC
89.219.64.0	89.219.127.255	Iran Telecommunication Company PJS
89.219.192.0	89.219.255.255	Iran Telecommunication Company PJS
89.221.80.0	89.221.95.255	Dade Samane Fanava Company (PJS)
89.235.80.0	89.235.95.255	Iran Telecommunication Company PJS
89.235.112.0	89.235.127.255	Iran Telecommunication Company PJS
91.98.0.0	91.98.255.255	Pars Online PJS
91.106.64.0	91.106.95.255	PJSC "Badr Rayan Jonoob
91.108.128.0	91.108.159.255	Rayaneh Gostar Farzanegan Ahwaz Company LTD.
91.133.128.0	91.133.255.255	Mobile Communication Company of Iran PLC
91.147.64.0	91.147.79.255	Iran Telecommunication Company PJS
91.184.64.0	91.184.79.255	Farabord Dadeh Haye Iranian Co.
91.184.80.0	91.184.95.255	Datak Company LLC
91.185.128.0	91.185.159.255	Iran Telecommunication Company PJS
91.186.192.0	91.186.223.255	Raya Sepehr Vira Data Processing Company Ltd.
91.243.160.0	91.243.175.255	07/08/12
91.250.224.0	91.250.239.255	Iran Telecommunication Company PJS
91.251.0.0	91.251.255.255	Mobile Communication Company of Iran PLC
92.114.16.0	92.114.31.255	Mobin Net Communication Company (Private Joint Stock)
92.114.64.0	92.114.79.255	Rightel Communication Service Company PJS
92.242.192.0	92.242.223.255	Respina Networks & Beyond PJSC
93.110.0.0	93.110.255.255	Mobile Communication Company of Iran PLC
93.113.224.0	93.113.239.255	Afranet
93.114.16.0	93.114.31.255	Rightel Communication Service Company PJS
93.115.224.0	93.115.239.255	Iran Telecommunication Company PJS
93.117.0.0	93.117.31.255	Iran Telecommunication Company PJS
93.117.32.0	93.117.47.255	Iran Telecommunication Company PJS
93.117.96.0	93.117.127.255	Iran Telecommunication Company PJS
93.117.176.0	93.117.191.255	Mobile Communication Company of Iran PLC
93.118.96.0	93.118.127.255	Iran Telecommunication Company PJS
93.118.128.0	93.118.159.255	Iran Telecommunication Company PJS
93.118.160.0	93.118.175.255	Iran Telecommunication Company PJS
93.119.32.0	93.119.63.255	Iran Telecommunication Company PJS
93.119.64.0	93.119.95.255	Iran Telecommunication Company PJS
93.119.208.0	93.119.223.255	Mobile Communication Company of Iran PLC
93.126.0.0	93.126.63.255	09/07/08
94.24.0.0	94.24.15.255	Rightel Communication Service Company PJS
94.24.80.0	94.24.95.255	Rightel Communication Service Company PJS
94.74.128.0	94.74.191.255	Farahoosh Dena PLC
94.101.128.0	94.101.143.255	Mobin Net Communication Company (Private Joint Stock)
94.101.176.0	94.101.191.255	Noyan Abr Arvan Co. ( Private Joint Stock)
94.101.240.0	94.101.255.255	Mobile Communication Company of Iran PLC
94.139.160.0	94.139.175.255	Farabord Dadeh Haye Iranian Co.
94.139.176.0	94.139.191.255	Datak Company LLC
94.182.0.0	94.183.255.255	Aria Shatel Company Ltd
94.184.0.0	94.184.127.255	Institute for Research in Fundamental Sciences
94.184.128.0	94.184.255.255	Institute for Research in Fundamental Sciences
94.241.128.0	94.241.191.255	Raya Sepehr Vira Data Processing Company Ltd.
95.38.0.0	95.38.255.255	Fanava Group
95.64.0.0	95.64.127.255	Mobile Communication Company of Iran PLC
95.80.128.0	95.80.191.255	Bozorg Net-e Aria
95.81.64.0	95.81.127.255	Hamara System Tabriz Engineering Company
95.142.224.0	95.142.239.255	Armaghan Rahe Talaie
95.162.0.0	95.162.255.255	Rightel Communication Service Company PJS
109.72.192.0	109.72.207.255	khalij fars Ettela Resan Company J.S.
109.74.224.0	109.74.239.255	Iran Telecommunication Company PJS
109.108.160.0	109.108.191.255	Mobile Communication Company of Iran PLC
109.109.32.0	109.109.63.255	ANDISHE SABZ KHAZAR CO. P.J.S.
109.110.160.0	109.110.191.255	Shabdiz Telecom Network PJSC
109.122.224.0	109.122.239.255	Asiatech Data Transmission company
109.122.240.0	109.122.255.255	MIHAN COMMUNICATION SYSTEMS CO.,LTD
109.125.128.0	109.125.159.255	Pishgaman Tejarat Sayar Company (Private Joint Stock)
109.125.160.0	109.125.191.255	Pishgaman Toseeh Ertebatat Company (Private Joint Stock)
109.162.128.0	109.162.255.255	Datak Company LLC
109.201.0.0	109.201.31.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
109.203.128.0	109.203.159.255	Mobile Communication Company of Iran PLC
109.203.160.0	109.203.191.255	Farahoosh Dena PLC
109.225.128.0	109.225.191.255	Mobile Communication Company of Iran PLC
109.230.64.0	109.230.95.255	Boomerang Rayaneh
109.238.176.0	109.238.191.255	khalij fars Ettela Resan Company J.S.
109.239.0.0	109.239.15.255	Mehvar Machine
113.203.0.0	113.203.127.255	Mobile Communication Company of Iran PLC
128.65.160.0	128.65.175.255	Shabakeh Gostar Dorna Cooperative Co.
128.65.176.0	128.65.191.255	Asiatech Data Transmission company
130.255.192.0	130.255.255.255	Mobile Communication Company of Iran PLC
151.232.0.0	151.235.255.255	Iran Telecommunication Company PJS
151.238.0.0	151.239.255.255	Aria Shatel Company Ltd
151.240.0.0	151.247.255.255	Aria Shatel Company Ltd
158.58.0.0	158.58.127.255	Mobile Communication Company of Iran PLC
159.20.96.0	159.20.111.255	ANDISHE SABZ KHAZAR CO. P.J.S.
164.138.128.0	164.138.191.255	Mobile Communication Company of Iran PLC
164.215.128.0	164.215.255.255	Fanava Group
172.80.128.0	172.80.255.255	Mobile Communication Company of Iran PLC
176.12.64.0	176.12.79.255	ANDISHE SABZ KHAZAR CO. P.J.S.
176.46.128.0	176.46.159.255	Farahoosh Dena PLC
176.65.160.0	176.65.191.255	Iran Telecommunication Company PJS
176.65.192.0	176.65.223.255	Mobile Communication Company of Iran PLC
176.65.224.0	176.65.239.255	Iran Telecommunication Company PJS
176.67.64.0	176.67.79.255	khalij fars Ettela Resan Company J.S.
176.101.32.0	176.101.47.255	03/11/11
176.102.224.0	176.102.255.255	29/11/11
176.123.64.0	176.123.127.255	27/07/12
178.131.0.0	178.131.255.255	Mobile Communication Company of Iran PLC
178.169.0.0	178.169.31.255	Pars Online PJS
178.173.128.0	178.173.191.255	Shiraz Hamyar Co.
178.173.192.0	178.173.223.255	Shiraz Hamyar Co.
178.215.0.0	178.215.63.255	28/06/10
178.219.224.0	178.219.239.255	28/06/10
178.236.96.0	178.236.111.255	Iran Telecommunication Company PJS
178.238.192.0	178.238.207.255	Iran Telecommunication Company PJS
178.239.144.0	178.239.159.255	Toloe Rayaneh Loghman Educational and Cultural Co.
178.252.128.0	178.252.191.255	GOSTARESH-E-ERTEBATAT-E MABNA COMPANY (Private Joint Stock)
178.253.0.0	178.253.63.255	Raya Sepehr Vira Data Processing Company Ltd.
188.75.64.0	188.75.127.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
188.118.64.0	188.118.127.255	University of Tehran
188.121.96.0	188.121.127.255	Noyan Abr Arvan Co. ( Private Joint Stock)
188.121.128.0	188.121.159.255	Farabord Dadeh Haye Iranian Co.
188.122.96.0	188.122.127.255	Mobile Communication Company of Iran PLC
188.136.144.0	188.136.159.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
188.136.160.0	188.136.191.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
188.136.192.0	188.136.223.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
188.158.0.0	188.158.255.255	Parvaresh Dadeha Co. Private Joint Stock
188.159.0.0	188.159.127.255	Parvaresh Dadeha Co. Private Joint Stock
188.159.128.0	188.159.191.255	Parvaresh Dadeha Co. Private Joint Stock
188.159.192.0	188.159.223.255	Parvaresh Dadeha Co. Private Joint Stock
188.208.64.0	188.208.95.255	Rightel Communication Service Company PJS
188.208.144.0	188.208.159.255	Rightel Communication Service Company PJS
188.208.160.0	188.208.191.255	Rightel Communication Service Company PJS
188.208.224.0	188.208.255.255	Rightel Communication Service Company PJS
188.209.16.0	188.209.31.255	Iran Telecommunication Company PJS
188.209.32.0	188.209.47.255	Iran Telecommunication Company PJS
188.209.64.0	188.209.79.255	Iran Telecommunication Company PJS
188.209.128.0	188.209.143.255	Ideh Pardazan Tarh Sepidar co.(LTD)
188.209.192.0	188.209.207.255	Mobile Communication Company of Iran PLC
188.210.64.0	188.210.79.255	Mobile Communication Company of Iran PLC
188.210.96.0	188.210.127.255	Iran Telecommunication Company PJS
188.210.128.0	188.210.191.255	Iran Telecommunication Company PJS
188.210.192.0	188.210.207.255	Mobile Communication Company of Iran PLC
188.211.0.0	188.211.15.255	Mobile Communication Company of Iran PLC
188.211.32.0	188.211.63.255	Iran Telecommunication Company PJS
188.211.64.0	188.211.127.255	Iran Telecommunication Company PJS
188.211.128.0	188.211.159.255	Iran Telecommunication Company PJS
188.211.176.0	188.211.191.255	Iran Telecommunication Company PJS
188.211.192.0	188.211.223.255	Iran Telecommunication Company PJS
188.212.48.0	188.212.63.255	Mobile Communication Company of Iran PLC
188.212.64.0	188.212.95.255	Iran Telecommunication Company PJS
188.212.160.0	188.212.191.255	Iran Telecommunication Company PJS
188.212.208.0	188.212.223.255	Iran Telecommunication Company PJS
188.212.224.0	188.212.239.255	Iran Telecommunication Company PJS
188.213.64.0	188.213.79.255	Mobin Net Communication Company (Private Joint Stock)
188.213.96.0	188.213.127.255	Iran Telecommunication Company PJS
188.213.144.0	188.213.159.255	Iran Telecommunication Company PJS
188.213.176.0	188.213.191.255	Iran Telecommunication Company PJS
188.214.160.0	188.214.191.255	Iran Telecommunication Company PJS
188.215.128.0	188.215.143.255	Iran Telecommunication Company PJS
188.215.160.0	188.215.191.255	Iran Telecommunication Company PJS
188.215.192.0	188.215.223.255	Iran Telecommunication Company PJS
188.229.0.0	188.229.127.255	Mobile Communication Company of Iran PLC
188.253.32.0	188.253.63.255	Pishgaman Toseeh Ertebatat Company (Private Joint Stock)
188.253.64.0	188.253.95.255	Pishgaman Toseeh Ertebatat Company (Private Joint Stock)
192.15.0.0	192.15.255.255	Mobile Communication Company of Iran PLC
193.151.128.0	193.151.159.255	Asiatech Data Transmission company
194.225.0.0	194.225.255.255	Institute for Research in Fundamental Sciences
195.146.32.0	195.146.63.255	Information Technology Company (ITC)
195.181.0.0	195.181.31.255	Iran Telecommunication Company PJS
195.181.32.0	195.181.63.255	Iran Telecommunication Company PJS
195.181.64.0	195.181.127.255	Iran Telecommunication Company PJS
204.18.0.0	204.18.255.255	Mobile Communication Company of Iran PLC
212.16.64.0	212.16.95.255	Farhang Azma Communications Company LTD
212.33.192.0	212.33.207.255	Asiatech Data Transmission company
212.33.208.0	212.33.223.255	Iran Telecommunication Company PJS
212.80.0.0	212.80.31.255	Farhang Azma Communications Company LTD
212.86.64.0	212.86.95.255	Homaye Jahan Nama Co. ( Private Joint Stock)
212.120.192.0	212.120.223.255	Hamara System Tabriz Engineering Company
213.109.240.0	213.109.255.255	Iran Telecommunication Company PJS
213.176.0.0	213.176.31.255	Iranian Research Organization for Science & Technology
213.176.32.0	213.176.63.255	Iranian Research Organization for Science & Technology
213.176.64.0	213.176.127.255	Iranian Research Organization for Science & Technology
213.195.0.0	213.195.15.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
213.195.32.0	213.195.47.255	Rightel Communication Service Company PJS
213.207.192.0	213.207.207.255	Farabord Dadeh Haye Iranian Co.
213.207.208.0	213.207.223.255	Datak Company LLC
213.207.224.0	213.207.255.255	Datak Company LLC
213.217.32.0	213.217.63.255	Pars Online PJS
213.233.160.0	213.233.191.255	Sharif University Of Technology
217.11.16.0	217.11.31.255	Afranet
217.24.144.0	217.24.159.255	Iran Telecommunication Company PJS
217.25.48.0	217.25.63.255	IRNA
217.60.0.0	217.60.255.255	Aria Shatel Company Ltd
217.66.192.0	217.66.207.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
217.66.208.0	217.66.223.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
217.77.112.0	217.77.127.255	Iran Telecommunication Company PJS
217.146.208.0	217.146.223.255	Tose'h Fanavari Ertebabat Pasargad Arian Co. PJS
217.170.240.0	217.170.255.255	Petiak System Co JSC
217.174.16.0	217.174.31.255	National Iranian Oil Company
217.218.0.0	217.219.255.255	Information Technology Company (ITC)
`

func init() {
	lines := strings.Split(strings.TrimSpace(rawIRRanges), "\n")
	irRanges = make([]irRange, 0, len(lines))
	for _, l := range lines {
		l = strings.TrimSpace(l)
		if l == "" {
			continue
		}
		parts := strings.Split(l, "\t")
		if len(parts) < 3 {
			continue
		}
		from := net.ParseIP(parts[0])
		to := net.ParseIP(parts[1])
		if from != nil && to != nil {
			irRanges = append(irRanges, irRange{
				from: from,
				to:   to,
				isp:  parts[2],
			})
		}
	}
}

// LookupIranISP checks if the given IP address falls within any of the static Iranian ISP IP blocks.
// If it does, it returns the ISP owner's name and true.
func LookupIranISP(ipStr string) (string, bool) {
	ip := net.ParseIP(strings.TrimSpace(ipStr))
	if ip == nil {
		return "", false
	}
	ip16 := ip.To16()
	if ip16 == nil {
		return "", false
	}
	for _, r := range irRanges {
		from16 := r.from.To16()
		to16 := r.to.To16()
		if from16 != nil && to16 != nil {
			if bytes.Compare(ip16, from16) >= 0 && bytes.Compare(ip16, to16) <= 0 {
				return r.isp, true
			}
		}
	}
	return "", false
}
