// Server.java


package com.example;

import static spark.Spark.post;
import static spark.Spark.port;
import com.google.gson.JsonSyntaxException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.model.terminal.Reader;
import com.stripe.net.Webhook;

import io.github.cdimascio.dotenv.Dotenv;

public class Server {
    public static void main(String[] args) {
        // The library needs to be configured with your account's secret key.
        // Ensure the key is kept out of any version control system you might be using.

        Dotenv dotenv = Dotenv.configure().load();
        Stripe.stripeVersion += "terminal_collect_inputs_beta=v1;terminal_collect_confirm_beta=v1";
        Stripe.apiKey = dotenv.get("STRIPE_SECRET");
        // This is your Stripe CLI webhook secret for testing your endpoint locally.
        String endpointSecret = dotenv.get("WEBHOOK_SECRET");
        

        port(4242);

        post("/webhook", (request, response) -> {
            String payload = request.body();
            String sigHeader = request.headers("Stripe-Signature");
            Event event = null;

            try {
                event = Webhook.constructEvent(
                    payload, sigHeader, endpointSecret
                );
            } catch (JsonSyntaxException e) {
                // Invalid payload
                response.status(400);
                return "";
            } catch (SignatureVerificationException e) {
                // Invalid signature
                response.status(400);
                return "";
            }

            // Deserialize the nested object inside the event
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;
            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
            } else {
                // Deserialization failed, probably due to an API version mismatch.
                // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
                // instructions on how to handle this case, or return an error here.
            }
            // Handle the event
            switch (event.getType()) {
              case "terminal.reader.action_failed": {
                // Then define and call a function to handle the event terminal.reader.action_failed
                Reader reader = (Reader) stripeObject;

                System.out.println(reader.toString()); 
                break;
              }
              case "terminal.reader.action_succeeded": {
                // Then define and call a function to handle the event terminal.reader.action_succeeded
                Reader reader = (Reader) stripeObject;

                System.out.println(reader.toString()); 
                break;
              }
              // Primarily for Collect/Confirm
              case "terminal.reader.action_updated": {
                // Then define and call a function to handle the event terminal.reader.action_updated
                Reader reader = (Reader) stripeObject;

                System.out.println(reader.toString()); 
                break;
              }
              // ... handle other event types
              default:
                System.out.println("Unhandled event type: " + event.getType());
            }

            response.status(200);
            return "";
        });
    }
}