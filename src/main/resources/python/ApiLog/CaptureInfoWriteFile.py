import json
import os

class CaptureInfoWriteFile:
    def __init__(self):
        self.history_file = 'request_history.json'  # 指定历史请求文件名
        self.static_ext = ['js', 'css', 'ico', 'jpg', 'png', 'gif', 'jpeg', 'bmp', 'conf']

        # 如果历史请求文件不存在，则创建一个空文件
        if not os.path.exists(self.history_file):
            with open(self.history_file, 'w') as history_file:
                history_file.write('[]')

    def request(self, flow):
        flow_request = flow.request
        url = flow_request.url
        path = flow_request.path
        method = flow_request.method
        text = flow_request.get_text()
        last_path = url.split('?')[0].split('/')[-1]
        
        if flow_request.host != "tmall.up.railway.app":
            return
        
        if last_path.split('.')[-1] in self.static_ext:
            return
        
        if not text:
            return
        
        # 读取历史请求文件
        with open(self.history_file, 'r') as history_file:
            history_data = json.load(history_file)
        
        # 检查是否存在具有相同路径和方法的条目
        for item in history_data:
            if item['path'] == path and item['method'] == method:
                # 合并请求文本到现有条目
                item['request_text'] = self._merge_request_text(item['request_text'], text)
                break
        else:
            # 如果不存在，则添加新条目
            data = {
                'path': path,
                'method': method,
                'request_text': text
            }
            history_data.append(data)
        
        # 写入历史请求文件
        with open(self.history_file, 'w') as history_file:
            json.dump(history_data, history_file, indent=2, default=str)
    
    def _merge_request_text(self, existing_text, new_text):
        try:
            existing_data = json.loads(existing_text)
            new_data = json.loads(new_text)
            if isinstance(existing_data, list):
                if isinstance(new_data, list):
                    return json.dumps(existing_data + new_data)
                else:
                    existing_data.append(new_data)
                    return json.dumps(existing_data)
            else:
                if isinstance(new_data, list):
                    new_data.append(existing_data)
                    return json.dumps(new_data)
                else:
                    return json.dumps([existing_data, new_data])
        except json.JSONDecodeError:
            # 如果现有文本不是JSON格式，则将新文本作为单独的请求保存
            return existing_text + '\n' + new_text

addons = [CaptureInfoWriteFile()]
