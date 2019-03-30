require './ruby_lab.rb'

RSpec.describe "Lab Questions Check" do
  context "unique_tracks with stop words removed" do
    process_file("unique_tracks.txt")

    it "11. Song title starting with 'amore'" do
      expect(create_title('amore').rstrip()).to eq('amore mio babbino caro gianni schicchi')
    end

    it "12. Song title starting with 'love'" do
      expect(create_title('love').rstrip()).to eq('love you are you are you are you are you are you are you are you are you are you')
    end

    it "13. Song title starting with 'little'" do
      expect(create_title('little').rstrip()).to eq('little girl like you are you are you are you are you are you are you are you are you')
    end
  end
end
