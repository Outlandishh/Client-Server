import kivy
import socket
import sys
import webbrowser


kivy.require('2.0.0')  # current kivy version


from kivy.app import App
from kivy.core.window import Window
from kivy.uix.floatlayout import FloatLayout
from kivy.properties import StringProperty
from kivy.graphics import *


class FloatLayout(FloatLayout):
    Window.size = (360, 200)
    status_text = StringProperty()
    ip_address = StringProperty()

    def connect_to_server(self, instance, value):
        self.status_text = 'Not Connected'

        # https://realpython.com/python-sockets/#echo-client
        HOST = self.ip.text  # the server's hostname or IP address
        PORT = 6789  # port used by the server
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.settimeout(0.1)
            s.connect((HOST, PORT))
            self.status_text = 'Connection Test Successful \nDrag and Drop Files to Send'

            # Drag and drop functionality:
            Window.bind(on_dropfile=self._on_file_drop)

            if value != '':
                sent = str(s.send(value.encode()))
                if sent == 0:
                    self.status_text = "Failure: 0 Characters Sent"
                else:
                    print(sent + 'Characters Sent Successfully')
                    print(value)
                    self.status_text = (sent + ' Characters Sent Successfully')
                print(s)

            s.shutdown(socket.SHUT_RDWR)
            s.close()
            print(s)

        except socket.timeout as e:
            self.status_text = ('Connection Test Failure: \n' + str(e))
            print(e)
        except socket.error as e:
            self.status_text = ('Connection Test Failure: \n' + str(e))
            print(e)
        return

    def _on_file_drop(self, window, file_path):
        print(file_path)
        data = open(file_path, 'r').read()  # reading file as string
        self.connect_to_server('', data)
        return

    def help(self):
        webbrowser.open('help.html', new=2, autoraise=True)

    def exit(self):
        sys.exit()

    def __init__(self, **kwargs):
        super(FloatLayout, self).__init__(**kwargs)

        with self.canvas:
            # Draw a red rectangle:
            Color(0, 0, 1, 0.5, mode="rgba")
            self.rect = Rectangle(pos=(310, 0), size=(50, 50))
            # Draw a white star:
            Color(255, 255, 255, 1, mode="rgba")
            Line(points=(320, 5, 335, 45, 350, 5, 315, 30, 355, 30, 320, 5))


class MyApp(App):
    def build(self):
        self.title = 'Python client'
        return FloatLayout()


if __name__ == '__main__':
    MyApp().run()
