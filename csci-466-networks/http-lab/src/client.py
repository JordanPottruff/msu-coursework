import httplib, urllib, sys;
from sys import argv;
import re;

def fire(address, port, x, y):

    params = urllib.urlencode({'x':x, 'y':y});
    headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "plain/text"};
    conn = httplib.HTTPConnection(address+":"+port, timeout=1000);
    conn.request("POST", "", params, headers);
    response = conn.getresponse();
    handleResponse(int(x), int(y), response.status, response.reason);
    repeat(address, port)

def handleResponse(x, y, status, reason):
    if(int(status) == 200):
        hit = re.search('hit=1', reason);
        if hit:
            print('hit');
            sink = re.search('sink=([A-Z])', reason);
            if sink:
                updateOpponentBoard(x, y, sink.group(1));
                print('sink');
            else:
                updateOpponentBoard(x, y, 'H');
        else:
            updateOpponentBoard(x, y, 'M');
            print('miss');
    else:
        print str(status) + ': ' + reason;

def updateOpponentBoard(x, y, symbol):
    board = fileToBoard('opponent_board.txt');
    board[y][x] = symbol;
    boardToFile('opponent_board.txt', board);

#Takes a txt file and creates a board represented as a 2D list
def fileToBoard(filename):
    board_file = open(filename, 'r');
    board = [];
    for i in range(10):
        line = list(board_file.readline().strip());
        board.append(line);
    board_file.close();
    return board;

def boardToFile(filename, board):
    board_file = open(filename, 'w');
    for i in range(10):
        for j in range(10):
            board_file.write(board[i][j]);
        board_file.write('\n');
    board_file.close();

def repeat(address, port):
    x = raw_input("Enter q to quit \n x : ")
    if x == 'q':
        sys.exit()
    y = raw_input("y: ")
    fire(address, port, x, y)

if __name__ == "__main__":
    if len(argv) == 5:
        #NOTE: we have to allow invalid coordinates here, because the server
        #needs to issue a 400 error for that, rather than the client handling it.
        fire(argv[1], argv[2], argv[3], argv[4]);
    else:
        print "Invalid number of arguments";
