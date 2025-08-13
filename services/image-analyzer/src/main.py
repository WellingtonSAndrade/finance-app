from src.config import PromptTemplate

def main():
    prompt = PromptTemplate.build("a")
    print(prompt)

if __name__ == "__main__":
    main()
