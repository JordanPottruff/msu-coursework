require "./ruby_lab"

RSpec.describe "Title Cleaning Self Check" do
  context "Only A Named titles" do
    it "counts are 52760" do
      file_name = "a_tracks.txt"
      count = 0

      if RUBY_PLATFORM.downcase.include? 'mswin'
        file = File.open(file_name)
        unless file.eof?
          file.each_line do |line|
            song = cleanup_title(line)

            if not song.nil?
              song.match(/^[\d\w\s']+$/) do |m|
                count += 1
              end
            end
          end
        end
        file.close
      else
        IO.foreach(file_name) do |line|
          song = cleanup_title(line)

          if not song.nil?
    				song.match(/^[\d\w\s']+$/) do |m|
              count += 1
            end
          end
        end
      end
      expect(count).to eq(52760)
    end
  end
end
