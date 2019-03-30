require './ruby_lab'

RSpec.describe "Lab Questions Check" do
  context "Fixed the problem" do
    process_file("unique_tracks.txt")

    it "17. Song title starting with word 'montana'" do
      expect(create_title 'montana').to eq('montana sacra nova scotia')
      expect(create_title 'montana').to eq('montana sacra nova baby i love you are')
    end

    it "18. Song title starting with word 'bob'" do
      expect(create_title 'bob').to eq('bob marley song you are')
      expect(create_title 'bob').to eq('bob marley vs world is my heart')
      expect(create_title 'bob').to eq('bob marley me down road blues night long way you are')
      expect(create_title 'bob').to eq('bob marley purt drive me down road blues night long way you are')
      expect(create_title 'bob').to eq('bob marley medley 1 2')
    end
  end
end
