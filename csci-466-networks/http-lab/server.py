from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer;
from sys import argv;
import urlparse;
import json;
import re;

own_board = [];
OPPONENT_BOARD = "opponent_board.txt";

#Implementation of BaseHTTPRequestHandler for Battleship game
class BattleshipRequestHandler(BaseHTTPRequestHandler):

    def _set_headers(self, code, response=''):
        self.send_response(code, response);
        self.end_headers();

    def _response_miss(self):
        self._set_headers(200, 'hit=0');

    def _response_hit(self):
        self._set_headers(200, 'hit=1');

    def _response_sink(self, ship):
        self._set_headers(200, 'hit=1\&sink='+ship);

    def _response_not_found(self):
        self._set_headers(404, 'invalid coordinates');

    def _response_gone(self):
        self._set_headers(410, 'already fired on');

    def _response_bad_request(self):
        self._set_headers(400, 'bad request');

    def _determine_response(self, raw_data):
        #Regex formats of url encoding for battleship game
        format1 = 'x=[0-9]&y=[0-9]'; #x,y
        format2 = 'y=[0-9]&x=[0-9]'; #y,x
        if re.match(format1, raw_data) or re.match(format2, raw_data):
            #Data safe to parse and restructure into dictionary
            parsed_data = dict(urlparse.parse_qsl(raw_data));
            x = int(parsed_data['x']);
            y = int(parsed_data['y']);

            symbol = own_board[y][x];   #get the symbol at (x,y)
            own_board[y][x] = 'X';      #set the location with X to denote a guess
            if(symbol == 'X'):
                #already guessed (x,y)
                self._response_gone();
            elif(symbol == '_'):
                #miss
                self._response_miss();
            else:
                #hit
                if(any(symbol in sublist for sublist in own_board)):
                    #just a hit, ship still has spots remaining
                    self._response_hit();
                else:
                    #otherwise no spots remain for the ship, so its sunk
                    self._response_sink(symbol);

        else:
            self._response_bad_request();

    #POST mechanics for the client to send moves
    def do_POST(self):
        raw_post_data = self.rfile.read(int(self.headers['Content-Length']));
        print raw_post_data;
        self._determine_response(raw_post_data);

    #GET mechanics for handling the viewing of boards as html pages
    def do_GET(self):
        print self.path;
        #check for HTTP get request for own_board.html page
        if(self.path == '/own_board.html'):
            self._set_headers(200);
            self.wfile.write("<html><body><h1>Your Board</h1>");

            for i in range(9):
                for j in range(9):
                    self.wfile.write("<p style='display:inline;'>%s</p>" % own_board[i][j]);
                self.wfile.write("<br>");

            self.wfile.write("<p></p>");
            self.wfile.write("</body></html>");

            #TODO: Change the above html example to include a basic table that
            #shows the player's own board.

        #check for HTTP get request for opponent_board.html page
        elif(self.path == '/opponent_board.html'):
            self._set_headers(200);
            self.wfile.write("<html><body><h1>Opponent's Board</h1></body>");
            for i in range(9):
                for j in range(9):
                    self.wfile.write("<p style='display:inline;'>%s</p>" % fileToBoard(OPPONENT_BOARD)[i][j]);
                self.wfile.write("<br>");

            self.wfile.write("<p></p>");
            self.wfile.write("</body></html>");
            #TODO: Change the above html example to include a basic table that
            #shows the opponent's board.

        #handle all other GET paths with a 404 Not Found response
        else:
            self._set_headers(404, "Not Found");

#Takes a txt file and creates a board represented as a 2D list
def fileToBoard(filename):
    board_file = open(filename, 'r');
    board = [];
    for i in range(10):
        line = list(board_file.readline().strip());
        board.append(line);
    board_file.close();
    return board;

def generateOpponentBoard(filename):
    board_file = open(filename, 'w+');
    board = [];
    for i in range(10):
        line = '_'*10;
        board_file.write(line+'\n');
        board.append(list(line));
    board_file.close();
    return board;

#Runs the HTTP server
def run(port):
    server_address = ('', port);
    httpd = HTTPServer(server_address, BattleshipRequestHandler);
    print 'Starting battleship httpd server...';
    httpd.serve_forever();

#Main method
if __name__ == "__main__":
    if len(argv) == 3:
        generateOpponentBoard(OPPONENT_BOARD);
        own_board = fileToBoard(argv[2]);
        run(int(argv[1]));
    else:
        print 'Invalid number of arguments';
