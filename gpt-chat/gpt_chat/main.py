import openai
import os
import asyncio
from aiohttp import ClientSession
from dotenv import load_dotenv
from typing import Union

model = "gpt-3.5-turbo"

debug = False

system_prompt = {
    "role": "system",
    "content": "",
}

history = []

tokens_used = {"prompt": 0, "completion": 0, "total": 0}


def get_user_input():
    """Get user's input, then process it."""
    user_input = input("\nYou: ")
    process_flag = process_commands(user_input)

    # if process_flag is a string, return it
    if isinstance(process_flag, str):
        return process_flag

    # otherwise, use it as a bool to determine if it's a normal message
    if process_flag:
        history.append(
            {
                "role": "user",
                "content": user_input,
            }
        )
        return True


def process_commands(user_input) -> Union[bool, str]:
    """Process special user commands, return True if it's a normal message."""
    input = user_input.strip().lower()
    if not input.startswith("/"):
        return True

    match input:
        case "/help":
            print("TODO help output, list commands")
        case "/exit":
            return "exit"
        case "/tokens":
            print("TODO: print tokens if sending now, and total used")
        case "/clear":
            print("TODO: clear history after a confirmation")
        case "/retry":
            print("TODO: retry last message")
        case _:
            # It's a normal message
            return True

    return False


async def get_ai_response() -> bool:
    # TODO validate under max tokens first
    try:
        response = await openai.ChatCompletion.acreate(
            model=model,
            messages=[system_prompt] + history,
        )
    except Exception as e:
        print(f"AI: Error: {e}\n")
        return False

    if debug:
        print(f"\nAI DEBUG: full response: {response}\n")

    if response.get("error"):
        print(f"AI: Error: {response['error']}\n")
        return False

    model_choices = response.get("choices", [{}])[0]
    model_response = model_choices.get("message", {}).get("content")
    if not model_response:
        print(f"AI: Error: no text completion, full response: {response}\n")
        return False

    history.append(
        {
            "role": "assistant",
            "content": model_response.strip(),
            "tokens": response.get("usage"),
            "time": response.get("created"),
            "stop": model_choices.get("finish_reason"),
        }
    )

    return True


def print_ai_response(response):
    """Print AI's most recent response, if there is one."""
    tokens = response.get("tokens", {})
    total, prompt, completion = (
        tokens.get("total_tokens"),
        tokens.get("prompt_tokens"),
        tokens.get("completion_tokens"),
    )
    print(f"AI Usage: Prompt {prompt} / Completion {completion} / Total {total}\n")
    stop = response.get("stop")
    if stop == "content_filter":
        print("AI Stop Reason: Content Filter Flagged\n")
    print(f"AI: {response.get('content')}\n")
    if stop == "length":
        print("AI Stop Reason: Token Limit\n")


async def main():
    """Main chat loop."""
    # Provide persistent session so it doesn't need to keep recreating its own
    openai.aiosession.set(ClientSession())

    try:
        while True:
            do_next = get_user_input()
            if do_next == "exit":
                break
            # TODO spinner?
            do_next = await get_ai_response() if do_next else do_next
            if do_next and (response := history[-1]).get("role") == "assistant":
                print_ai_response(response)

    # print error when anything is caught
    except Exception as e:
        print(f"\nUnknown error: {e}")

    finally:
        await openai.aiosession.get().close()


if __name__ == "__main__":
    load_dotenv()
    api_key = os.getenv("API_KEY")
    if not api_key:
        print("Error: API_KEY not set in environment or .env file")
        exit(1)
    openai.api_key = api_key

    asyncio.run(main())
