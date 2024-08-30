use simple_benchmark;
create TABLE tbl2 (
	pdate DATE,
	hour INT,
	fuel TINYINT,
	qty INT,
	amt int,
	PRIMARY KEY(`pdate`,`hour`,`fuel`)
);
create TABLE tbl3 (
	pdate DATE,
	city VARCHAR(100),
	total_amt DOUBLE,
	PRIMARY KEY(`pdate`,`city`)
);
