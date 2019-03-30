
#!/usr/bin/ruby
###############################################################
#
# CSCI 305 - Ruby Programming Lab
#
# Jordan Pottruff
# jordanpottruff@gmail.com
#
###############################################################

$bigrams = Hash.new(0)# The Bigram data structure
$name = "Jordan Pottruff"

# function to process each line of a file and extract the song titles
def process_file(file_name)
	puts "Processing File.... "

	begin
		if RUBY_PLATFORM.downcase.include? 'mswin'
			file = File.open(file_name)
			unless file.eof?
				file.each_line do |line|
					# do something for each line (if using windows)
				end
			end
			file.close
		else
			IO.foreach(file_name, encoding: "utf-8") do |line|
				# do something for each line (if using macos or linux)
				title = cleanup_title(line)
				title.match(/([\d\w\s\']+)/)
				title = title.downcase
				if !$1.nil? && $1.length == title.length
					words = title.gsub(/\s+/m, ' ').strip.split(" ")
					words = filter_stops(words)
					previous = ''
					for word in words
						unless previous.empty?
							if $bigrams[previous] == 0
								$bigrams[previous] = Hash.new(0);
								$bigrams[previous][word] = 1
							else
								$bigrams[previous][word] = $bigrams[previous][word]+1
							end
						end
						previous = word
					end
				else

				end
			end
		end

		puts "Finished. Bigram model built.\n"
	rescue
		STDERR.puts "Could not open file"
		exit 4
	end
	puts $bigrams['love'].length;
end

def filter_stops(words)
	stop_words = ["a", "an", "and", "by", "for", "from", "in", "of", "on", "or", "out", "the", "to", "with"]
	words -= stop_words
end

def mcw(word)
	max_value = -1
	max_key = ""

	if $bigrams[word] == 0
		return ""
	end


	$bigrams[word].each do |key, value|
		if value > max_value
			max_value = value
			max_key = key
		end
		if value == max_value
			if [true, false].sample
				max_value = value
				max_key = key
			end
		end
	end
	max_key
end

def cleanup_title(line)
	line.match(/(\w{18})<SEP>(\w{18})<SEP>(.*)<SEP>(.*)/)
	title = "#{$4}"
	title = title.gsub(/[\(\[\{\\\/\_\-\:\"\`\+\=\*].*/, "")
	title = title.gsub(/feat\..*/, "")
	title = title.gsub(/[\?\!\.\;\&\@\%\#\|]/, "")
	title = title.force_encoding("UTF-8")
	title = title.gsub(/[¿¡]/u, "")
	title;
end

def create_title(line)
	word = line
	title = "";
	list = Hash.new(0)
	loop do
		if(title != "")
			title += " "
		end
		if(list[word] != 0)
			break
		end
		title += word
		list[word] = 1
		word = mcw(word)
		break if word == ""
	end
	title;
end

# Executes the program
def main_loop()
	puts "CSCI 305 Ruby Lab submitted by #{$name}"

	if ARGV.length < 1
		puts "You must specify the file name as the argument."
		exit 4
	end

	# process the file
	process_file(ARGV[0])

	# Get user input
	loop do
		puts "Enter a word [Enter 'q' to quit]:"
		word = STDIN.gets.chomp
		if word == "q"
			break
		end
		title = create_title(word)
		puts title
	end
end

if __FILE__==$0
	main_loop()
end
