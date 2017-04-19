#!/usr/bin/perl
#
# File:    rencopy
# Purpose: outputs script to rename (\d) .jpg to -0\d.jpg
# Author:  laned
#
# $Header: C:\\hold\\cvsrepo2/rencopy.pl,v 1.1 2017/04/18 10:29:23 laned Exp $
#
# ----------------------------------------------------------------------

use strict;
use File::Glob ':glob';

my $VER = '$Id: rencopy.pl,v 1.1 2017/04/18 10:29:23 laned Exp $';

select STDERR; $| = 1;  # flush stderr automatically
select STDOUT; $| = 1;  # flush stdout automatically

# ----------------------------------------------------------------------

my $dirsep = "\\";
my $c_char = "REM";
my $move_cmd = "MOVE";
my $dir_cmd = "DIR";
my $copy_cmd = " COPY /Y";
my $mkdir_cmd = " MKDIR ";

my $ONE_MEG = 1 * 1000 * 1000;
my $max_size = undef; # -ms
my $silent = 0; # -s
my $long_list = 0; # -l

if ( &is_unix() )
{
   $dirsep = "/";
   $c_char = "#";
   $dir_cmd = "ls";
   $move_cmd = "/bin/mv";
   $copy_cmd = " /bin/cp -f -p";
   $mkdir_cmd = "mkdir -p ";
}

# ----------------------------------------------------------------------

my $verbose = 0;
my $veryVerbose = 0;
my $recurse = 0;
my $infilename = "";
my $entry;

my @filelist = & parse_args ( @ARGV );

while ( <*.jpg> )
{
	if ( /\((\d)\)/ )
	{
		my $digit = $1;
		my $newname = $_;
		$newname =~ s/ \($digit\)/-0$digit/;

		if ( $_ eq $newname)
		{
			print qq~Rem same "$_" "$newname"\n~;
		}
		else
		{
			print qq~move "$_" "$newname"\n~;
		}
	}
}



exit 0;

if ( int(@filelist) == 0 )
{
  &processSTDIN();
}
else
{

  @filelist = &glob_filelist ();

  foreach $entry ( @filelist )
  {
    if ( -d $entry )
    {
      & handledir ( $entry, $recurse);
    }
    elsif ( -f $entry )
    {
      my $filename = $entry;
      $filename =~ s/.*\///;

      my $dirname = $entry;
      $dirname = substr ($dirname, 0, length ($entry) - length($filename))
          if $filename ne "";

      $dirname =~ s/\/$//;

      & handlefile ( $dirname, $filename, $entry);
    }
    elsif ( $entry eq "--" )
    {
      &processSTDIN();
    }
    else
    {
      print STDERR "File not found: $entry\n";
    }
  }
}

# ----------------------------------------------------------------------
# processSTDIN
#
sub glob_filelist
{
	my @list = ();
	foreach $entry ( @filelist )
	{
		if ( $entry =~ m/[?*]/ )
		{
			my @sources = bsd_glob($entry);

			if ( int(@sources) == 0)
			{
				print STDERR "No files match $entry\n";
				exit 3;
			}
			else
			{
				@list = (@list, @sources);
			}
		}
		else
		{
			push (@list, $entry);
		}
	}

  return @list;

}

# ----------------------------------------------------------------------
# processSTDIN
#
sub processSTDIN
{
		my @list = <STDIN>;
		my ($recs) = &handlelist ("(stdin)", @list);
		print STDERR "EOF: stdin has $recs lines\n" if $veryVerbose;
}


# ----------------------------------------------------------------------
# handlelist filename list(of ines)
#
sub handlelist
{
	my ($filename, @list) = @_;

	my $recs = 0;

	foreach my $line (@list)
	{
		$recs ++;
		&handleLine ( $filename, $recs, $line);
	}

	return $recs;
}

# ----------------------------------------------------------------------
# load_list filename
# returns list of lines removes comment and blank lines
#
sub load_list
{
	my ($filename) = @_;
	my (@outlist) = ();
	my ($recno);

	if (open (IFN2, "$filename"))
	{
			while (<IFN2>)
			{
				$recno ++;
				chomp;

				s/\s*$//; # remove trailing space

				next if /^\s*#/;  # remove '#' comment lines
				next if /^\s*;/;  # remove ';' comment lines
				next if /^$/; 		# remove blank lines

				push (@outlist, $_);
				print "$filename: $recno: '$_'\n" if $veryVerbose;
			}
	}
	else
	{
		print STDERR "Could not open file $filename\n";
	}

	return @outlist;
}

# ----------------------------------------------------------------------
# handlefile dirname, filename, fullfilename
#
sub handlefile
{
	my ($dirname, $fname, $fullname) = @_;
	my ($recs);

	print "handlefile: $fullname, $dirname, $fname\n" if $veryVerbose;

	if ( ! open (IFN, "$fullname") )
	{
		print STDERR "Could not open $fullname\n";
		return;
	}

	print STDERR "File: $fullname\n" if $veryVerbose;

	my @list = <IFN>;

	&handlelist ($fname, @list);

	print STDERR "EOF: $fname has $recs lines\n" if $veryVerbose;

	close (IFN);
}


# ----------------------------------------------------------------------
# handledir ( dirname, recurse )
#
sub handledir
{
	my ( $dirname, $recurse ) = @_;

	my ( @direntries );
	my ( $filename );

	print "Dir: $dirname\n" if $veryVerbose;

  $dirname =~ s/^\.\///;

	opendir (DP, $dirname );
	@direntries = readdir (DP);
	closedir (DP);

  if ( $dirname eq "." )
  {
    $dirname = "";
  }
  else
  {
    $dirname = "$dirname/";
  }

	my $thefn;

	foreach $filename (sort @direntries)
	{
		$thefn = "${dirname}$filename";

		if ( -f $thefn )
		{
			& handlefile ( $dirname, $filename, $thefn);
		}
	}

	if ( $recurse )
	{
		foreach $filename (sort @direntries)
		{
			next if $filename eq ".";
			next if $filename eq "..";
			next if $filename eq "";

			$thefn = "${dirname}$filename";

			if ( -d $thefn )
			{
				& handledir ( $thefn, $recurse );
			}
		}
	}
}


# ----------------------------------------------------------------------
# handleLine filename, recordNumber, line
#

sub handleLine
{
	my ($filename, $recnumber, $line) = @_;
	chomp ($line);


}

# ------------------------------------------------------------
#
#
sub dosify
{
	my ($name) = @_;
	$name =~s/\//${dirsep}/g if ! is_unix();
	return $name;
}

# ------------------------------------------------------------
#
#
sub is_unix
{
   # return ( -f "/etc/passwd");
   # return  -f "/bin/find" || -f "/usr/bin/find";
   return  -f "/bin/mount";
}


# ----------------------------------------------------------------------
#
# print_usage
#
sub print_usage
{
	print <<SAGEU;
Usage: $0 filelist
Usage: $0 [-r] [-v] [--] 

Purpose outputs script to rename (\d) .jpg to -0\d.jpg

Reads current directory

-h     this message

-v     more information

--     read from stdin

$VER
SAGEU
	exit 4;
}

# ----------------------------------------------------------------------

sub	parse_args
	# ***
	# --- PULL KEY VALUES FROM ARGUMENT LIST ---
	# ***
{
	my( @args) = @_;
	my (@list);
	my @tmp_args;

	# ( int(@args) == 0 ) && &print_usage();

	for ( @tmp_args = @args; $#tmp_args >= 0; shift @tmp_args)
		{

		if ( $tmp_args[0] eq "-h" || $tmp_args[0] eq "-help" ||
			 $tmp_args[0] eq "-usage" || $tmp_args[0] eq "-version" )
			{
			& print_usage();
			}

		elsif ( $tmp_args[ 0 ] eq '-if')
			{
			$infilename = $tmp_args[ 1 ];
			die ("Missing filename after -if") if $infilename eq "";
			die ("Can not find filename \"$infilename\"after -if") if ! -f $infilename;
			shift @tmp_args;
			}
		elsif ( $tmp_args[ 0 ] eq '-r')
			{
			$recurse = 1;
			}
		elsif ( $tmp_args[ 0 ] eq '-V')
			{
			$veryVerbose = 1;
			}
		elsif ( $tmp_args[ 0 ] eq '-v')
			{
			$verbose = 1;
			}
		elsif ( substr ($tmp_args[ 0 ], 0, 1)  eq '-')
			{
			die("unknown or invalid option $tmp_args[0]\n");
			}
		else
			{
				print "pushing $tmp_args[0] \n" if $veryVerbose;
				push (@list, $tmp_args [ 0 ] );
			}
		}  # scan each command line argument
		return @list
}

#
# $Log: rencopy.pl,v $
# Revision 1.1  2017/04/18 10:29:23  laned
# Add to CVS
#
#
# vi ts=3:sw=3:ai:ic
# eof rencopy  $Id: rencopy.pl,v 1.1 2017/04/18 10:29:23 laned Exp $
