import itertools
import re

# 讀取原始 feature 文件
def read_feature_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        return file.read()

# 解析 feature 文件中的 Scenario Outline 和 Examples
def parse_feature_file(content):
    scenario_outline_pattern = re.compile(r'(Scenario Outline:.*?Examples:\n(\s*\|.*?\|.*?\n)+)', re.S)
    scenarios = scenario_outline_pattern.findall(content)
    return scenarios

# 解析 Example 表格
def parse_examples(example_text):
    lines = example_text.strip().split('\n')
    headers = [header.strip() for header in lines[0].split('|') if header.strip()]
    values = [line.strip().split('|')[1:-1] for line in lines[1:]]
    examples = [dict(zip(headers, [value.strip() for value in value_list])) for value_list in values]
    return headers, examples

# 生成組合測試的 Examples
def generate_combinations(examples):
    keys = examples[0].keys()
    values = [list({example[key] for example in examples}) for key in keys]
    combinations = [dict(zip(keys, combination)) for combination in itertools.product(*values)]
    return combinations

# 生成新的 Example 表格
def generate_example_table(headers, examples):
    header_row = ' | '.join(headers)
    rows = [header_row] + [' | '.join(example[header] for header in headers) for example in examples]
    return '\n'.join(f'      | {row} |' for row in rows)

# 更新 feature 文件
def update_feature_file(content, scenarios, new_examples):
    updated_content = content
    for scenario, new_example_table in zip(scenarios, new_examples):
        old_example_section = re.search(r'Examples:\n(\s*\|.*?\|.*?\n)+', scenario[0]).group(0)
        new_example_section = f'Examples:\n{new_example_table}\n'
        updated_scenario = scenario[0].replace(old_example_section, new_example_section)
        updated_content = updated_content.replace(scenario[0], updated_scenario)
    return updated_content

# 寫入新的 feature 文件
def write_feature_file(file_path, content):
    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(content)

# 驗證組合測試的 Examples
def validate_combinations(headers, examples, combinations):
    expected_combinations = list(itertools.product(*[list({example[header] for example in examples}) for header in headers]))
    actual_combinations = [tuple(combination[header] for header in headers) for combination in combinations]
    return set(expected_combinations) == set(actual_combinations)

# 主函數
def main(input_file, output_file):
    content = read_feature_file(input_file)
    
    print("Reading feature file content:")
    print(content)
    
    scenarios = parse_feature_file(content)
    
    print("Parsed scenarios:")
    for scenario in scenarios:
        print(scenario)
    
    new_examples = []

    for scenario in scenarios:
        example_text = re.search(r'Examples:\n(\s*\|.*?\|.*?\n)+', scenario[0]).group(0)
        headers, examples = parse_examples(example_text.split('Examples:\n')[1])
        combinations = generate_combinations(examples)
        
        # 驗證組合測試的 Examples
        if not validate_combinations(headers, examples, combinations):
            raise ValueError("生成的組合測試的 Examples 不符合組合測試規則")
        
        new_example_table = generate_example_table(headers, combinations)
        new_examples.append(new_example_table)

    updated_content = update_feature_file(content, scenarios, new_examples)
    
    print("Updated feature file content:")
    print(updated_content)
    
    write_feature_file(output_file, updated_content)

if __name__ == '__main__':
    input_file = 'original.feature'  # 請替換為你的輸入文件路徑
    output_file = 'output.feature'  # 請替換為你想輸出的文件路徑
    main(input_file, output_file)
