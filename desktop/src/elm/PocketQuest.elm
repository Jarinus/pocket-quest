module PocketQuest exposing (main)

import Dict exposing (Dict)
import Html exposing (Html)
import Html.Events as Events
import Html.Attributes as Attributes
import Json.Decode as Decode
import Material
import Material.Tabs as Tabs
import Material.Color as Color
import Material.Layout as Layout
import Material.Options as Options
import Material.Elevation as Elevation
import Material.Typography as Typo
import Mouse exposing (Position)
import Inventory


type alias Model =
    { inventoryPanel : Panel
    , inventory : Inventory.Model
    , mdl : Material.Model
    }


type alias Panel =
    { tabIndex : Int
    , tabs : Dict Int PanelTab
    , width : Int
    , minWidth : Int
    , maxWidth : Int
    , drag : Maybe Drag
    }


type alias PanelTab =
    { header : String
    , content : Html Msg
    }


type alias Drag =
    { start : Position
    , current : Position
    }


type Msg
    = SelectInventoryPanelTab Panel Int
    | InventoryMsg Inventory.Msg
    | OnInventoryPanelResizeStart Position
    | OnInventoryPanelResize Position
    | OnInventoryPanelResizeEnd Position
    | Mdl (Material.Msg Msg)



-- Main


main : Program Never Model Msg
main =
    Html.program
        { init = init
        , subscriptions = subscriptions
        , update = update
        , view = view
        }



-- Init


init : ( Model, Cmd Msg )
init =
    let
        inventoryWidth =
            360

        inventory =
            Inventory.init inventoryWidth

        inventoryPanel =
            { tabIndex = 0
            , tabs =
                [ { header = "Inventory"
                  , content = viewInventory inventory
                  }
                ]
                    |> List.indexedMap (,)
                    |> Dict.fromList
            , width = inventoryWidth
            , minWidth = inventoryWidth
            , maxWidth = 600
            , drag = Nothing
            }
    in
        { inventoryPanel = inventoryPanel
        , inventory = inventory
        , mdl = Material.model
        }
            ! []


mapInventoryMsg : Inventory.Msg -> Msg
mapInventoryMsg msg =
    InventoryMsg msg



-- Update


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        SelectInventoryPanelTab panel index ->
            { model
                | inventoryPanel =
                    { panel | tabIndex = index }
            }
                ! []

        InventoryMsg msg_ ->
            { model
                | inventory =
                    Inventory.update msg_ model.inventory
            }
                ! []

        OnInventoryPanelResizeStart position ->
            let
                inventoryPanel =
                    model.inventoryPanel
            in
                { model
                    | inventoryPanel =
                        { inventoryPanel
                            | drag = Just (Drag position position)
                        }
                }
                    ! []

        OnInventoryPanelResize position ->
            case model.inventoryPanel.drag of
                Just drag ->
                    let
                        inventoryPanel =
                            model.inventoryPanel

                        xDifference =
                            position.x - drag.current.x

                        newWidth =
                            inventoryPanel.width + xDifference
                    in
                        if (newWidth < inventoryPanel.minWidth) || (newWidth > inventoryPanel.maxWidth) then
                            model ! []
                        else
                            let
                                inventory =
                                    Inventory.update (Inventory.NewRowWidth newWidth) model.inventory
                            in
                                { model
                                    | inventoryPanel =
                                        { inventoryPanel
                                            | drag =
                                                Drag drag.start position
                                                    |> Just
                                            , width = newWidth
                                            , tabs =
                                                Dict.update 0
                                                    (\tab ->
                                                        case tab of
                                                            Just tab_ ->
                                                                Just { tab_ | content = viewInventory inventory }

                                                            Nothing ->
                                                                tab
                                                    )
                                                    inventoryPanel.tabs
                                        }
                                    , inventory =
                                        inventory
                                }
                                    ! []

                Nothing ->
                    model ! []

        OnInventoryPanelResizeEnd position ->
            let
                inventoryPanel =
                    model.inventoryPanel
            in
                { model
                    | inventoryPanel =
                        { inventoryPanel
                            | drag = Nothing
                        }
                }
                    ! []

        Mdl msg_ ->
            Material.update Mdl msg_ model



-- Subscriptions


subscriptions : Model -> Sub Msg
subscriptions model =
    case model.inventoryPanel.drag of
        Nothing ->
            Sub.none

        Just _ ->
            Sub.batch
                [ Mouse.moves OnInventoryPanelResize
                , Mouse.ups OnInventoryPanelResizeEnd
                ]



-- View


view : Model -> Html Msg
view { mdl, inventoryPanel } =
    viewPanel
        mdl
        inventoryPanel
        (SelectInventoryPanelTab inventoryPanel)


viewInventory : Inventory.Model -> Html Msg
viewInventory inventory =
    Inventory.view inventory
        |> Html.map mapInventoryMsg


viewPanel : Material.Model -> Panel -> (Int -> Msg) -> Html Msg
viewPanel mdl panel onSelectTab =
    if Dict.size panel.tabs > 0 then
        viewPanel_ mdl panel onSelectTab
    else
        Html.text ""


viewPanel_ : Material.Model -> Panel -> (Int -> Msg) -> Html Msg
viewPanel_ mdl panel onSelectTab =
    let
        tabProperties =
            [ Options.cs "overlay overlay-left"
            , Tabs.onSelectTab onSelectTab
            , Tabs.activeTab panel.tabIndex
            ]

        tabLabels =
            Dict.values panel.tabs
                |> List.map (\tab -> tab.header)
                |> List.map viewTabLabel

        tabContent =
            case Dict.get panel.tabIndex panel.tabs of
                Just tab ->
                    [ tab.content ]

                Nothing ->
                    []
    in
        Options.div
            [ Options.cs "overlay-wrapper"
            , Attributes.style
                [ ( "width", (toString panel.width) ++ "px" )
                , ( "min-width", (toString panel.minWidth) ++ "px" )
                , ( "max-width", (toString panel.maxWidth) ++ "px" )
                ]
                |> Options.attribute
            ]
            [ Tabs.render Mdl
                [ 0 ]
                mdl
                tabProperties
                tabLabels
                tabContent
            , viewOverlayResizeHandle panel
            ]


viewTabLabel : String -> Tabs.Label Msg
viewTabLabel header =
    Tabs.label
        [ Options.cs "overlay-tab"
        , Options.center
        , Color.background Color.primary
        , Color.text Color.white
        ]
        [ Html.text header ]


viewOverlayResizeHandle : Panel -> Html Msg
viewOverlayResizeHandle panel =
    Options.div
        [ Options.css "width" "4px"
        , Options.css "position" "absolute"
        , Options.css "top" "0"
        , Options.css "bottom" "0"
        , Options.css "right" "0"
        , Options.css "background" "black"
        , Options.css "cursor" "col-resize"
        , Events.on "mousedown" (Decode.map OnInventoryPanelResizeStart Mouse.position)
            |> Options.attribute
        ]
        []
