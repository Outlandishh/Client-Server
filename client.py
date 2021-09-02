# Import the libraries that are needed for the program
import kivy
import socket
import sys
import webbrowser

# The current Kivy version is 2.0.0
kivy.require('2.0.0')  # current kivy version

# Specific modules used from the libraries imported above
from kivy.app import App
from kivy.core.window import Window
from kivy.uix.floatlayout import FloatLayout
from kivy.properties import StringProperty
from kivy.graphics import *

# Create the GUI, assign a size for the display
class FloatLayout(FloatLayout):
    Window.size = (360, 200)
    status_text = StringProperty()
    ip_address = StringProperty()

    # Function for finding and connecting to the server
    def connect_to_server(self, instance, value):
        # Default position is 'Not Connected'
        self.status_text = 'Not Connected'

        # Define the host/IP address and port numbers
        HOST = self.ip.text  # The server's hostname or IP address
        PORT = 6789  # Port used by the server

        # Create the socket and attempt to connect to the server
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.settimeout(0.1)
            s.connect((HOST, PORT))
            self.status_text = 'Connection Test Successful \nDrag and Drop Files to Send'

            # Add drag and drop functionality:
            Window.bind(on_dropfile=self._on_file_drop)

            # Sending and receiving data
            # If the client detects an empty input value, display
            # that nothing was sent. When the input values are
            # correctly sent, output how many characters were sent
            if value != '':
                sent = str(s.send(value.encode()))
                if sent == 0:
                    self.status_text = "Failure: 0 Characters Sent"
                else:
                    print(sent + 'Characters Sent Successfully')
                    print(value)
                    self.status_text = (sent + ' Characters Sent Successfully')
                print(s)

            # Shut the socket down and close the connection
            s.shutdown(socket.SHUT_RDWR)
            s.close()
            print(s)

        # Exception handling for timeouts or no connectivity
        except socket.timeout as e:
            self.status_text = ('Connection Timeout Failure: \n' + str(e))
            print(e)
        except socket.error as e:
            self.status_text = ('Connection Not Found: \n' + str(e))
            print(e)
        return

    # Functionality for adding documents to the server.
    # When a file is dropped on the window, read the file
    # and send the data to the connect_to_server function
    # who in turn forwards it to the server
    def _on_file_drop(self, window, file_path):
        print(file_path)
        data = open(file_path, 'r').read()  # Read the file as string
        self.connect_to_server('', data)
        return

    # Create a help menu button to open the 'help.html' file hosted locally
    # on my machine in the /var/www/html folder.
    def help(self):
        webbrowser.open('http://192.168.10.112/help.html', new=2, autoraise=True)

    # Create the exit function
    def exit(self):
        sys.exit()

    # Initialise the GUI
    def __init__(self, **kwargs):
        super(FloatLayout, self).__init__(**kwargs)

        # Add 2D graphics to make the program just
        # that little bit prettier... kinda
        with self.canvas:
            # Draw a red rectangle:
            Color(0, 0, 1, 0.5, mode="rgba")
            self.rect = Rectangle(pos=(310, 0), size=(50, 50))
            # Draw a white star:
            Color(255, 255, 255, 1, mode="rgba")
            Line(points=(320, 5, 335, 45, 350, 5, 315, 30, 355, 30, 320, 5))


# Build the application
class MyApp(App):
    def build(self):
        self.title = 'Python client'
        return FloatLayout()


if __name__ == '__main__':
    MyApp().run()
