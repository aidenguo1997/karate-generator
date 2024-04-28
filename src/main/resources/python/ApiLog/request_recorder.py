# filename: request_recorder.py
import csv
from mitmproxy import http

class RequestRecorder:
    def __init__(self):
        self.records = []
    
    def request(self, flow: http.HTTPFlow):
        if "tmall.up.railway.app/" in flow.request.url:
            url = flow.request.url  # 接口url
            host = flow.request.host  # 域名
            path = flow.request.path  # 接口地址   
            method = flow.request.method
            text = flow.request.text
            record = {
                "URL": url,
                "Host": host,
                "Path": path,
                "Method": method,
                "Request": text
            }
            self.records.append(record)
    def response(self, flow: http.HTTPFlow):
        if "tmall.up.railway.app/" in flow.request.url:
            flow_response = flow.response  # 获取响应对象
            response_text = flow_response.text  # 响应体
            record = {
                "Response": response_text
            }
            self.records.append(record)
    
    def done(self):
        with open("api_requests.csv", "w", newline="", encoding="utf-8") as csvfile:
            fieldnames = ["URL", "Host", "Path", "Method", "Request", "Response"]
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for record in self.records:
                writer.writerow(record)

addons = [
    RequestRecorder()
]


