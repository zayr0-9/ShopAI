# ShopAI
LLM and ML vision powered shopping list app written in kotlin
Uses google ml kit to scan receipt and extract text from it. The text is then passed to Gemini to parse it since receipts are often folded and faded, 
google ml kit often gets the text wrong, but using an LLM we can bring back the lost context by just assuming what said item that was purchased should 
have been. Using ml kit to scan reduces LLM costs a lot since processing text for an LLM is still much cheaper than an image.
[Watch the Demo on YouTube](https://www.youtube.com/watch?v=0FroxO_maGI)

![Demo](test.gif) 
