require './ruby_lab'

RSpec.describe "Lab Questions Check" do
  context "unique_tracks" do
    process_file("unique_tracks.txt")

    it "1. Word most often following 'happy'" do
      expect(mcw 'happy').to eq('birthday')
    end

    it "2. Word most often following 'sad'" do
      expect(mcw 'sad').to eq('song')
    end

    it "3. Unique words following 'computer'" do
      expect($bigrams['computer'].count).to eq(58)
    end

    it "4. Word most often following 'computer'" do
      expect(mcw 'computer').to eq('love')
    end

    it "5. How often does this word follow 'computer'" do
      expect($bigrams['computer']['love']).to eq(13)
    end
  end
end
