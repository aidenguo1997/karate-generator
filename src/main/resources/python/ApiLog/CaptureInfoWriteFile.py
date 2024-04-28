import mitmproxy.http
import json



class CaptureInfoWriteFile:
    def __init__(self):
        self.output_file = 'captured_data.json'  # 指定输出文件名
        #self.url = None  # 初始化为 None
        # http static resource file extension
        self.static_ext = ['js', 'css', 'ico', 'jpg', 'png', 'gif', 'jpeg', 'bmp', 'conf']
        # 初始化一个空的列表，用于存储捕获到的信息
        self.data_list = []

    def is_json_response(self, response_text):
        if not response_text:
            return False  # 空字符串不是有效的 JSON

        try:
            # 尝试解析响应体为 JSON
            json.loads(response_text)
            return True
        except json.JSONDecodeError:
            return False
        
    def request(self, flow: mitmproxy.http.HTTPFlow):
        flow_request = flow.request
        self.url = flow_request.url
        self.path = flow_request.path
        self.method = flow_request.method
        self.text = flow_request.get_text()
        lastPath = self.url.split('?')[0].split('/')[-1]
        if flow.request.host != "127.0.0.1:8102":
            return
        
        if lastPath.split('.')[-1] in self.static_ext:
            return
        
        if not self.text:
            return
        
        data = {
            'path': self.path,
            'method': self.method,
            'request_text': self.text
        }
        self.data_list.append(data)
          

    def done(self):
        # 将列表中的信息写入JSON文件
        with open(self.output_file, 'w') as json_file:
            json.dump(self.data_list, json_file, indent=2, default=str)

addons = [CaptureInfoWriteFile()]
