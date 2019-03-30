require './ruby_lab'

RSpec.describe "First Title Generation Self Check without removing stop words" do
  context "Only A Named titles" do
    process_file("a_tracks.txt")

    it "Starting with word 'happy'" do
      expect(create_title 'happy').to eq('happy now the world of the world of the world of the world of the world of the world of')
    end

    it "Starting with word 'sad'" do
      expect(create_title 'sad').to eq('sad love song for you ready for you ready for you ready for you ready for you ready for you')
    end

    it "Starting with word 'computer'" do
      expect(create_title 'computer').to eq('computer')
    end
  end
end
