require "./ruby_lab"

RSpec.describe "Bigram Counting Self Check" do
  context "Only A Named titles" do
    process_file("a_tracks.txt")

    it "Most common word to follow 'happy' is 'now'" do
      expect(mcw 'happy').to eq('now')
    end

    it "Most common word to follow 'sad' is 'love'" do
      expect(mcw 'sad').to eq('love')
    end

    it "Most common word to follow 'love' is 'song'" do
      expect(mcw 'love').to eq('song')
    end

    it "80 distinct words follow 'love'" do
      expect($bigrams['love'].length).to eq(80)
    end

    it "'song' follows 'love' 33 times" do
      expect($bigrams['love']['song']).to eq(33)
    end
  end
end
