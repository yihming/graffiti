import gdax

public_client = gdax.PublicClient()

res1 = public_client.get_products()
btc = filter(lambda x: x["id"] == "BCH-BTC", list)
